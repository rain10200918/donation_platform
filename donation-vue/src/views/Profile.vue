<template>
  <div class="profile-page">
    <div class="profile-banner"></div>

    <div class="profile-container">
      <el-row :gutter="25">
        <el-col :span="8">
          <el-card class="user-main-card" shadow="hover">
            <div class="user-avatar-wrapper">
              <el-avatar :size="110" :src="userInfo.avatar || defaultAvatar" class="avatar-shadow" />
              <div class="user-status-dot" :class="{ active: userInfo.status === '1' }"></div>
            </div>

            <h2 class="user-nickname">{{ userInfo.nickName || '爱心人士' }}</h2>
            <div class="user-badge">
              <el-tag effect="dark" round :type="userInfo.userType === '2' ? 'warning' : 'danger'">
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
                <span class="lab">爱心捐赠</span>
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
                  <el-descriptions-item label="最后登录">{{ formatDate(userInfo.lastLoginAt) }}</el-descriptions-item>
                </el-descriptions>

                <div class="auth-section">
                  <div class="auth-status-box" :class="'status-' + (authInfo?.authStatus ?? 'none')">
                    <div class="auth-icon-area">
                      <el-icon v-if="authInfo?.authStatus === 1"><CircleCheckFilled /></el-icon>
                      <el-icon v-else><WarningFilled /></el-icon>
                    </div>
                    <div class="auth-text-area">
                      <h4 v-if="!authInfo">尚未进行实名认证</h4>
                      <h4 v-else-if="authInfo.authStatus === 0">认证信息审核中</h4>
                      <h4 v-else-if="authInfo.authStatus === 1">您已完成实名认证</h4>
                      <h4 v-else-if="authInfo.authStatus === 2">实名认证未通过</h4>
                      <p>{{ authInfo?.rejectReason || '实名认证能增加您的求助可信度' }}</p>
                    </div>
                    <el-button
                        :type="authInfo?.authStatus === 2 ? 'danger' : 'primary'"
                        round
                        v-if="authInfo?.authStatus !== 1"
                        @click="handleAuth"
                    >
                      {{ authInfo?.authStatus === 2 ? '重新提交' : (authInfo?.authStatus === 0 ? '查看详情' : '立即去认证') }}
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
                  <el-table-column label="筹款进度" width="160">
                    <template #default="scope">
                      <div class="prog-box">
                        <span class="prog-num">{{ calculatePercent(scope.row.raisedAmount, scope.row.targetAmount) }}%</span>
                        <el-progress
                            :percentage="calculatePercent(scope.row.raisedAmount, scope.row.targetAmount)"
                            :stroke-width="6"
                            :show-text="false"
                            color="#f56c6c"
                        />
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="getStatusTag(scope.row.status)" effect="plain" round>
                        {{ getStatusText(scope.row.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="120" align="center">
                    <template #default="scope">
                      <el-button size="small" link type="primary" @click="router.push(`/project/${scope.row.id}`)">详情</el-button>
                      <el-button v-if="scope.row.status === 1" size="small" link type="success" @click="handleUpdateProgress">更新</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="myProjects.length === 0" description="这里还没有故事，去发起求助吧" />
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
  User, Calendar, LocationFilled, Postcard,
  List, Trophy, CircleCheckFilled, WarningFilled
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

const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/info')
    if (res.code === "200") userInfo.value = res.data
  } finally { loading.value = false }
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

const getTypeText = (type) => ({ "1": '爱心求助者', "2": '公益机构', "3": '爱心企业' }[type] || '普通用户')
const getStatusText = (s) => ({ 0: '审核中', 1: '募集中', 2: '已完成', 3: '已关闭' }[s] || '未知')
const getStatusTag = (s) => ({ 0: 'info', 1: 'danger', 2: 'success', 3: 'warning' }[s] || 'info')
const formatDate = (d) => d ? d.replace('T', ' ').split('.')[0] : '-'
const calculatePercent = (r, t) => t ? Math.min(Math.floor((r / t) * 100), 100) : 0

const handleEdit = () => ElMessage.info('编辑功能开发中...')
const handleAuth = () => router.push('/user-auth')
const handleUpdateProgress = () => ElMessage.success('请在项目详情页发布最新动态')

onMounted(() => {
  fetchUserInfo()
  fetchAuthInfo()
  fetchMyProjects()
})
</script>

<style scoped>
.profile-page { background-color: #f6f8fa; min-height: 100vh; }

.profile-banner {
  height: 160px;
  background: linear-gradient(120deg, #f56c6c 0%, #ff9a9e 100%);
}

.profile-container { padding: 0 10% 50px; margin-top: -60px; }

/* 左侧卡片美化 */
.user-main-card { border-radius: 16px; text-align: center; padding: 20px 10px; border: none; }
.user-avatar-wrapper { position: relative; display: inline-block; margin-bottom: 15px; }
.avatar-shadow { border: 4px solid #fff; box-shadow: 0 8px 20px rgba(0,0,0,0.1); }
.user-status-dot {
  position: absolute; bottom: 8px; right: 8px; width: 14px; height: 14px;
  background: #94a3b8; border: 2px solid #fff; border-radius: 50%;
}
.user-status-dot.active { background: #10b981; }
.user-nickname { font-size: 22px; color: #1e293b; margin: 0 0 10px; }
.user-stats-mini { display: flex; justify-content: space-around; margin: 20px 0; }
.stat-item { display: flex; flex-direction: column; }
.stat-item .num { font-size: 20px; font-weight: bold; color: #f56c6c; }
.stat-item .lab { font-size: 12px; color: #64748b; }
.user-info-list { text-align: left; padding: 0 15px; }
.info-item { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #475569; margin-bottom: 12px; }

/* 勋章卡片 */
.medal-card { margin-top: 20px; border-radius: 16px; border: none; }
.medal-header { display: flex; justify-content: space-between; font-weight: bold; }
.medal-icons { display: flex; gap: 15px; padding: 10px 0; }
.medal-img { width: 45px; height: 45px; transition: transform 0.3s; }
.medal-img:hover { transform: scale(1.1); }
.medal-img.grayscale { filter: grayscale(1); opacity: 0.3; }

/* 右侧内容美化 */
.content-card { border-radius: 16px; border: none; min-height: 500px; padding: 10px; }
.tab-label { display: flex; align-items: center; gap: 6px; font-size: 16px; }
.tab-content-header { display: flex; justify-content: space-between; align-items: center; margin: 15px 0; }
.tab-content-header h3 { margin: 0; color: #334155; }

/* 描述列表样式 */
.modern-desc :deep(.el-descriptions__label) { background: #f8fafc; color: #64748b; font-weight: bold; width: 120px; }

/* 实名认证引导区域 */
.auth-section { margin-top: 30px; }
.auth-status-box {
  display: flex; align-items: center; padding: 25px; border-radius: 12px; gap: 20px;
  background: #f8fafc; border: 1px solid #e2e8f0;
}
.auth-icon-area { font-size: 40px; }
.auth-text-area { flex-grow: 1; }
.auth-text-area h4 { margin: 0 0 5px; font-size: 18px; }
.auth-text-area p { margin: 0; color: #64748b; font-size: 14px; }

/* 不同认证状态的颜色 */
.status-1 { background: #f0fdf4; border-color: #bbf7d0; color: #166534; }
.status-1 .auth-icon-area { color: #22c55e; }
.status-0 { background: #eff6ff; border-color: #bfdbfe; color: #1e40af; }
.status-0 .auth-icon-area { color: #3b82f6; }
.status-2 { background: #fef2f2; border-color: #fecaca; color: #991b1b; }
.status-2 .auth-icon-area { color: #ef4444; }

/* 表格美化 */
.modern-table { margin-top: 10px; }
.prog-box { display: flex; flex-direction: column; gap: 4px; }
.prog-num { font-size: 12px; font-weight: bold; color: #f56c6c; }
</style>