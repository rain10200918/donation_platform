<template>
  <div class="profile-container">
    <el-breadcrumb separator="/" style="margin-bottom: 20px">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>个人中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card" v-loading="loading">
          <div class="user-avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
            <h2 class="user-nickname">{{ userInfo.nickName || '加载中...' }}</h2>

            <el-tag :type="userInfo.status === '1' ? 'success' : 'danger'">
              {{ userInfo.status === '1' ? '账号正常' : '账号禁用' }}
            </el-tag>
          </div>
          <el-divider />
          <div class="user-meta">
            <p><el-icon><User /></el-icon> 身份：{{ getTypeText(userInfo.userType) }}</p>
            <p><el-icon><Calendar /></el-icon> 注册：{{ formatDate(userInfo.createTime) }}</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>详细资料</span>
              <el-button type="primary" size="small" link @click="handleEdit">编辑信息</el-button>
            </div>
          </template>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户 ID">{{ userInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="手机号码">{{ userInfo.mobile }}</el-descriptions-item>
            <el-descriptions-item label="电子邮箱">{{ userInfo.email }}</el-descriptions-item>
            <el-descriptions-item label="最后登录">{{ formatDate(userInfo.lastLoginAt) }}</el-descriptions-item>
            <el-descriptions-item label="登录 IP">{{ userInfo.lastLoginIp === '0:0:0:0:0:0:0:1' ? '127.0.0.1' : userInfo.lastLoginIp }}</el-descriptions-item>
          </el-descriptions>

          <div class="action-bar">
            <el-alert
                title="完成实名认证后即可发起求助项目"
                type="warning"
                :closable="false"
                show-icon
                style="margin-bottom: 20px"
            />
            <el-button type="success" @click="handleAuth">去实名认证</el-button>
            <el-button type="info" plain @click="router.back()">返回首页</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Calendar } from '@element-plus/icons-vue'
import request from '../utils/request.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const userInfo = ref({})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 获取个人信息
const fetchUserInfo = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/info')
    if (res.code === "200") {
      userInfo.value = res.data
    } else {
      ElMessage.error(res.message || '获取失败')
    }
  } catch (err) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}

// 转换用户类型 (针对字符串 "1", "2", "3")
const getTypeText = (type) => {
  const map = { "1": '个人求助者', "2": '公益机构', "3": '企业赞助商' }
  return map[type] || '普通用户'
}

// 格式化日期：将 T 替换为空格，保持美观
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').split('.')[0]
}

const handleEdit = () => ElMessage.info('资料修改功能正在开发中...')
const handleAuth = () => router.push('/user-auth')

onMounted(fetchUserInfo)
</script>

<style scoped>
.profile-container { padding: 40px 12%; background-color: #f8fafc; min-height: 100vh; }
.user-card { text-align: center; border-radius: 12px; }
.user-avatar-section { display: flex; flex-direction: column; align-items: center; padding: 10px 0; }
.user-nickname { margin: 15px 0 8px; font-size: 22px; color: #1e293b; }
.user-meta { text-align: left; padding: 10px 20px; line-height: 2.2; color: #64748b; }
.user-meta .el-icon { margin-right: 10px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.action-bar { margin-top: 25px; }
:deep(.el-descriptions__label) { width: 140px; color: #475569; font-weight: 600; }
</style>