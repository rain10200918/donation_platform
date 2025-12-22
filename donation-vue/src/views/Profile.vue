<template>
  <div class="profile-page">
    <div class="profile-banner"></div>

    <div class="profile-container">
      <el-row :gutter="25">

        <el-col :span="8">
          <el-card class="user-main-card" shadow="hover">
            <div class="user-avatar-wrapper">
              <el-avatar :size="110" :src="userInfo.avatar || defaultAvatar" class="avatar-shadow" />
              <div class="user-status-dot active"></div>
            </div>

            <h2 class="user-nickname">{{ userInfo.nickName || '爱心人士' }}</h2>
            <div class="user-badge">
              <el-tag effect="dark" round type="danger">
                {{ getTypeText(userInfo.userType) }}
              </el-tag>
            </div>

            <div class="user-stats-mini">
              <div class="stat-item">
                <span class="num">{{ myProjects.length }}</span>
                <span class="lab">发起求助</span>
              </div>
              <el-divider direction="vertical" />
              <div class="stat-item">
                <span class="num">0</span>
                <span class="lab">获得捐助</span>
              </div>
            </div>

            <el-divider border-style="dashed" />

            <div class="user-info-list">
              <div class="info-item">
                <el-icon><Calendar /></el-icon>
                <span>加入时间：{{ formatDate(userInfo.createTime) }}</span>
              </div>
              <div class="info-item">
                <el-icon><LocationFilled /></el-icon>
                <span>注册 IP：{{ userInfo.lastLoginIp === '0:0:0:0:0:0:0:1' ? '127.0.0.1' : userInfo.lastLoginIp }}</span>
              </div>
            </div>
          </el-card>

          <el-card class="balance-card" shadow="hover">
            <div class="balance-header">
              <span class="label">账户可用余额</span>
              <el-button type="primary" link @click="rechargeVisible = true">
                <el-icon><Plus /></el-icon>充值
              </el-button>
            </div>
            <div class="balance-amount">
              <span class="currency">¥</span>
              <span class="value">{{ (userInfo.balance || 0).toFixed(2) }}</span>
            </div>
          </el-card>

          <el-card class="medal-card" shadow="never">
            <template #header>
              <div class="medal-header">
                <span>我的荣誉</span>
                <el-icon color="#E6A23C"><Trophy /></el-icon>
              </div>
            </template>
            <div class="medal-icons">
              <el-tooltip content="实名认证用户" placement="top">
                <img v-if="authInfo?.authStatus === 1" src="https://cdn-icons-png.flaticon.com/512/7641/7641727.png" class="medal-img" />
                <img v-else src="https://cdn-icons-png.flaticon.com/512/7641/7641727.png" class="medal-img grayscale" />
              </el-tooltip>
              <el-tooltip content="爱心求助先锋" placement="top">
                <img v-if="myProjects.length > 0" src="https://cdn-icons-png.flaticon.com/512/1040/1040230.png" class="medal-img" />
                <img v-else src="https://cdn-icons-png.flaticon.com/512/1040/1040230.png" class="medal-img grayscale" />
              </el-tooltip>
            </div>
          </el-card>
        </el-col>

        <el-col :span="16">
          <el-card class="content-card" shadow="never">
            <el-tabs v-model="activeTab" class="custom-tabs">

              <el-tab-pane name="info">
                <template #label>
                  <span class="tab-label"><el-icon><Postcard /></el-icon> 账号档案</span>
                </template>

                <div class="tab-content-header">
                  <h3>详细资料</h3>
                  <el-button type="primary" link @click="handleEdit">修改信息</el-button>
                </div>

                <el-descriptions :column="2" class="modern-desc" border>
                  <el-descriptions-item label="用户编号"># {{ userInfo.id }}</el-descriptions-item>
                  <el-descriptions-item label="手机号码">{{ userInfo.mobile }}</el-descriptions-item>
                  <el-descriptions-item label="电子邮箱">{{ userInfo.email || '未绑定' }}</el-descriptions-item>
                  <el-descriptions-item label="实名状态">
                    {{ authInfo?.authStatus === 1 ? '已认证' : '未认证' }}
                  </el-descriptions-item>
                </el-descriptions>

                <div class="auth-section">
                  <div class="auth-status-box" :class="'status-' + (authInfo?.authStatus ?? 'none')">
                    <div class="auth-icon-area">
                      <el-icon v-if="authInfo?.authStatus === 1"><CircleCheckFilled /></el-icon>
                      <el-icon v-else-if="authInfo?.authStatus === 0"><Timer /></el-icon>
                      <el-icon v-else><WarningFilled /></el-icon>
                    </div>
                    <div class="auth-text-area">
                      <h4 v-if="!authInfo">尚未进行实名认证</h4>
                      <h4 v-else-if="authInfo.authStatus === 0">认证信息审核中</h4>
                      <h4 v-else-if="authInfo.authStatus === 1">您已完成实名认证</h4>
                      <h4 v-else-if="authInfo.authStatus === 2">实名认证未通过</h4>
                      <p>{{ authInfo?.rejectReason || '实名认证后即可发起求助项目' }}</p>
                    </div>
                    <el-button
                        :type="authInfo?.authStatus === 2 ? 'danger' : 'primary'"
                        round
                        v-if="authInfo?.authStatus !== 1"
                        @click="handleAuth"
                    >
                      {{ authInfo?.authStatus === 2 ? '重新提交' : (authInfo?.authStatus === 0 ? '查看进度' : '立即认证') }}
                    </el-button>
                  </div>
                </div>
              </el-tab-pane>

              <el-tab-pane name="projects">
                <template #label>
                  <span class="tab-label"><el-icon><List /></el-icon> 我的求助</span>
                </template>

                <el-table :data="myProjects" stripe v-loading="projectLoading" class="modern-table">
                  <el-table-column prop="title" label="项目名称" min-width="180" show-overflow-tooltip />

                  <el-table-column label="进度" width="140">
                    <template #default="scope">
                      <div class="prog-box">
                        <span class="prog-num">{{ calculatePercent(scope.row.raisedAmount, scope.row.targetAmount) }}%</span>
                        <el-progress
                            :percentage="calculatePercent(scope.row.raisedAmount, scope.row.targetAmount)"
                            :stroke-width="6"
                            :show-text="false"
                            :color="scope.row.status === 2 ? '#67c23a' : '#f56c6c'"
                        />
                      </div>
                    </template>
                  </el-table-column>

                  <el-table-column label="状态" width="120">
                    <template #default="scope">
                      <el-tooltip v-if="scope.row.auditStatus === 2" :content="'驳回原因：' + (scope.row.rejectReason || '资料不全')" placement="top">
                        <el-tag type="danger" effect="dark" round style="cursor: help">审核驳回</el-tag>
                      </el-tooltip>
                      <el-tag v-else :type="getStatusTag(scope.row.status, scope.row.auditStatus)" effect="plain" round>
                        {{ getStatusText(scope.row.status, scope.row.auditStatus) }}
                      </el-tag>
                    </template>
                  </el-table-column>

                  <el-table-column label="操作" width="150" align="center">
                    <template #default="scope">
                      <template v-if="scope.row.auditStatus === 1 && (scope.row.status === 1 || scope.row.status === 2)">
                        <el-button size="small" link type="primary" @click="router.push(`/project/${scope.row.id}`)">详情</el-button>
                        <el-button v-if="scope.row.status === 1" size="small" link type="success" @click="handleUpdateProgress(scope.row)">更新</el-button>
                      </template>
                      <span v-else-if="scope.row.auditStatus === 0" class="op-status-text">
                        <el-icon><Timer /></el-icon> 审核中
                      </span>
                      <el-button v-else-if="scope.row.auditStatus === 2" size="small" type="danger" link @click="handleEditProject(scope.row)">
                        重新修改
                      </el-button>
                      <span v-else-if="scope.row.status === 3" class="op-status-text">已关闭</span>
                    </template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="myProjects.length === 0" description="暂未发起任何求助项目" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="rechargeVisible" title="账户充值" width="360px" class="recharge-dialog" align-center>
      <div class="recharge-body">
        <p class="recharge-tip">请选择充值金额 (模拟测试)</p>
        <el-radio-group v-model="rechargeAmount" class="recharge-grid">
          <el-radio-button :label="10">10元</el-radio-button>
          <el-radio-button :label="50">50元</el-radio-button>
          <el-radio-button :label="100">100元</el-radio-button>
          <el-radio-button :label="500">500元</el-radio-button>
        </el-radio-group>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rechargeVisible = false" round>取消</el-button>
          <el-button type="danger" :loading="rechargeLoading" @click="handleRecharge" round class="btn-full-width">
            立即充值
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="修改个人资料" width="500px">
      <el-form :model="editForm" label-width="80px" label-position="top">
        <el-form-item label="我的头像">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/file/upload"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              name="file"
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar-edit-preview" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">支持 jpg/png/jpeg，尺寸建议为正方形</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="editForm.nickName" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="editForm.email" placeholder="example@mail.com" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitEdit">保存修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Calendar, LocationFilled, Postcard, List, Trophy,
  CircleCheckFilled, WarningFilled, Timer, Plus
} from '@element-plus/icons-vue'
import request from '../utils/request.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const projectLoading = ref(false)
const activeTab = ref('info')
const userInfo = ref({})
const authInfo = ref(null)
const myProjects = ref([])
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const editDialogVisible = ref(false)
const submitLoading = ref(false)
const rechargeVisible = ref(false)
const rechargeAmount = ref(10)
const rechargeLoading = ref(false)
const editForm = ref({
  id: null,
  nickName: '',
  email: '',
  avatar: ''
})
const uploadHeaders = {
  Authorization: `Bearer ${sessionStorage.getItem('token')}`
}
const beforeAvatarUpload = (rawFile) => {
  const allowTypes = ['image/jpeg', 'image/png', 'image/jpg']
  if (!allowTypes.includes(rawFile.type)) {
    ElMessage.error('图片格式不正确！')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}
const handleRecharge = async () => {
  rechargeLoading.value = true
  try {
    // 这里的接口路径需要根据你的后端真实 Controller 路径修改
    const res = await request.post('/user/recharge', {
      amount: rechargeAmount.value
    })

    if (res.code === "200") {
      ElMessage.success(`充值成功！当前余额：¥${res.data.balance || userInfo.value.balance + rechargeAmount.value}`)
      rechargeVisible.value = false
      // 刷新用户信息以同步余额显示
      fetchUserInfo()
    }
  } catch (err) {
    console.error('充值失败', err)
  } finally {
    rechargeLoading.value = false
  }
}
// 上传成功后的回调
const handleAvatarSuccess = (response) => {
  if (response.code === "200") {
    // 假设你的后端 Result.success(url) 返回的数据在 data 字段
    editForm.value.avatar = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}
// 数据加载
const fetchUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.code === "200") userInfo.value = res.data
  } catch (err) {}
}

const fetchAuthInfo = async () => {
  try {
    const res = await request.get('/user-auth/detail')
    if (res.code === "200") authInfo.value = res.data
  } catch (err) {}
}

const fetchMyProjects = async () => {
  projectLoading.value = true
  try {
    const res = await request.get('/project/my-projects')
    if (res.code === "200") myProjects.value = res.data
  } finally { projectLoading.value = false }
}

// 映射逻辑
const getTypeText = (type) => ({ "1": '个人求助者', "2": '公益机构' }[type] || '认证企业')

const getStatusText = (s, audit) => {
  if (audit === 0) return '待审核'
  return { 1: '募集中', 2: '已完成', 3: '已关闭' }[s] || '未知'
}

const getStatusTag = (s, audit) => {
  if (audit === 0) return 'info'
  return { 1: 'danger', 2: 'success', 3: 'warning' }[s] || 'info'
}

const formatDate = (d) => d ? d.replace('T', ' ').split('.')[0] : '-'
const calculatePercent = (r, t) => t ? Math.min(Math.floor((r / t) * 100), 100) : 0


const handleAuth = () => router.push('/user-auth')
const handleUpdateProgress = (project) => {
  router.push({
    path: '/update-progress',
    query: { projectId: project.id, title: project.title }
  })
}
const handleEditProject = (project) => {
  ElMessage.warning('请联系管理员或在发布页重新提交申请')
  // 如果你有编辑页，可以执行：router.push(`/publish?editId=${project.id}`)
}
// 1. 点击“修改信息”按钮时，初始化表单
const handleEdit = () => {
  editForm.value = {
    id: userInfo.value.id,
    nickName: userInfo.value.nickName,
    email: userInfo.value.email,
    avatar: userInfo.value.avatar
  }
  editDialogVisible.value = true
}

// 2. 提交修改到后端
const submitEdit = async () => {
  if (!editForm.value.nickName) {
    return ElMessage.warning('昵称不能为空')
  }

  submitLoading.value = true
  try {
    const res = await request.post('/user/update', editForm.value)
    if (res.code === "200") {
      ElMessage.success('个人资料已更新')
      editDialogVisible.value = false
      // 重新拉取一次用户信息以刷新页面显示
      fetchUserInfo()
    }
  } catch (err) {
    // 错误已被拦截器处理
  } finally {
    submitLoading.value = false
  }
}
onMounted(() => {
  fetchUserInfo()
  fetchAuthInfo()
  fetchMyProjects()
})
</script>

<style scoped>
.profile-page { background-color: #f6f8fa; min-height: 100vh; }
.profile-banner { height: 160px; background: linear-gradient(120deg, #f56c6c 0%, #ff9a9e 100%); }
.profile-container { padding: 0 10% 50px; margin-top: -60px; }

/* 用户主卡片 */
.user-main-card { border-radius: 16px; text-align: center; padding: 20px 10px; border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.user-avatar-wrapper { position: relative; display: inline-block; margin-bottom: 15px; }
.avatar-shadow { border: 4px solid #fff; box-shadow: 0 8px 20px rgba(0,0,0,0.1); }
.user-status-dot { position: absolute; bottom: 8px; right: 8px; width: 14px; height: 14px; background: #94a3b8; border: 2px solid #fff; border-radius: 50%; }
.user-status-dot.active { background: #10b981; }
.user-nickname { font-size: 22px; color: #1e293b; margin: 0 0 10px; font-weight: bold; }
.user-stats-mini { display: flex; justify-content: space-around; margin: 20px 0; }
.stat-item .num { font-size: 20px; font-weight: bold; color: #f56c6c; display: block; }
.stat-item .lab { font-size: 12px; color: #64748b; }
.info-item { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #475569; margin-bottom: 12px; }

/* 勋章设计 */
.medal-card { margin-top: 20px; border-radius: 16px; border: none; }
.medal-header { display: flex; justify-content: space-between; font-weight: bold; align-items: center; }
.medal-icons { display: flex; gap: 15px; padding: 10px 0; }
.medal-img { width: 45px; height: 45px; transition: 0.3s; }
.medal-img.grayscale { filter: grayscale(1); opacity: 0.2; }

/* 内容区 */
.content-card { border-radius: 16px; border: none; min-height: 540px; }
.tab-label { display: flex; align-items: center; gap: 6px; font-size: 15px; font-weight: 600; }
.tab-content-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }

/* 认证状态框 */
.auth-status-box { display: flex; align-items: center; padding: 25px; border-radius: 12px; gap: 20px; margin-top: 30px; border: 1px solid #e2e8f0; }
.auth-icon-area { font-size: 36px; }
.auth-text-area { flex-grow: 1; }
.auth-text-area h4 { margin: 0 0 5px; font-size: 17px; color: #1e293b; }
.auth-text-area p { margin: 0; color: #64748b; font-size: 13px; }

/* 状态颜色 */
.status-1 { background: #f0fdf4; border-color: #bbf7d0; color: #166534; }
.status-0 { background: #eff6ff; border-color: #bfdbfe; color: #1e40af; }
.status-2 { background: #fef2f2; border-color: #fecaca; color: #991b1b; }

/* 表格辅助样式 */
.prog-box { display: flex; flex-direction: column; gap: 4px; }
.prog-num { font-size: 12px; font-weight: bold; color: #f56c6c; }
.op-status-text { font-size: 13px; color: #94a3b8; display: flex; align-items: center; justify-content: center; gap: 4px; }
.avatar-uploader {
  text-align: center;
  cursor: pointer;
}

.avatar-edit-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  transition: 0.2s;
  width: 100px;
  height: 100px;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #f56c6c;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
/* 余额卡片基础样式 */
.balance-card {
  margin-top: 20px;
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, #ffffff 0%, #fff5f5 100%);
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}

.balance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.balance-header .label {
  font-size: 13px;
  color: #94a3b8;
  letter-spacing: 1px;
}

.balance-amount {
  display: flex;
  align-items: baseline;
  gap: 6px;
  color: #f56c6c; /* 与主题色保持一致 */
}

.balance-amount .currency {
  font-size: 18px;
  font-weight: bold;
}

.balance-amount .value {
  font-size: 36px;
  font-weight: 800;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

/* 充值对话框样式 */
.recharge-body {
  padding: 10px 0;
}

.recharge-tip {
  margin-bottom: 15px;
  font-size: 14px;
  color: #64748b;
  text-align: center;
}

.recharge-grid {
  display: grid !important;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  width: 100%;
}

/* 强制修改 radio-button 样式使其撑满网格 */
.recharge-grid :deep(.el-radio-button) {
  width: 100%;
}
.recharge-grid :deep(.el-radio-button__inner) {
  width: 100%;
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  padding: 15px 0;
}

.btn-full-width {
  width: 100%;
  margin-top: 10px;
}

.recharge-dialog :deep(.el-dialog) {
  border-radius: 20px;
}
</style>