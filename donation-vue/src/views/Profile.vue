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
                        <el-button v-if="scope.row.status === 1" size="small" link type="success" @click="handleUpdateProgress">更新</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Calendar, LocationFilled, Postcard, List, Trophy,
  CircleCheckFilled, WarningFilled, Timer
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

// 操作逻辑
const handleEdit = () => ElMessage.info('详细资料修改功能暂未开放')
const handleAuth = () => router.push('/user-auth')
const handleUpdateProgress = () => ElMessage.success('请在项目详情页发布进展公告')
const handleEditProject = (project) => {
  ElMessage.warning('请联系管理员或在发布页重新提交申请')
  // 如果你有编辑页，可以执行：router.push(`/publish?editId=${project.id}`)
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
</style>