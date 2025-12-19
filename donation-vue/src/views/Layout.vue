<template>
  <div class="main-layout">
    <el-container>
      <el-header class="header">
        <div class="logo" @click="router.push('/')">
          <span class="heart-icon">❤️</span>
          <span class="logo-text">公益众筹平台</span>
        </div>

        <el-menu
            mode="horizontal"
            :ellipsis="false"
            class="menu"
            :default-active="route.path"
            router
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/projects">项目浏览</el-menu-item>
          <el-menu-item index="/news">信息公开</el-menu-item>

          <div class="flex-grow" />

          <div class="header-right">
            <el-button type="danger" class="publish-btn glow-btn" @click="handlePublish">
              发起求助
            </el-button>

            <el-dropdown v-if="token">
              <span class="user-info">
                <el-avatar :size="36" :src="userInfo.avatar || defaultAvatar" />
                <span class="username">{{ userInfo.nickName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="router.push('/my-projects')">我的求助</el-dropdown-item>
                  <el-dropdown-item divided @click="logout" style="color: #f56c6c">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button v-else type="primary" link @click="router.push('/login')">登录</el-button>
          </div>
        </el-menu>
      </el-header>

      <el-main class="page-container">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const route = useRoute()
const token = sessionStorage.getItem('token')
const userInfo = ref({ nickName: '加载中...', avatar: '' })
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const fetchUser = async () => {
  if (!token) return
  const res = await request.get('/user/info')
  if (res.code === "200") userInfo.value = res.data
}

/**
 * 核心逻辑：发起求助校验
 */
const handlePublish = async () => {
  if (!token) {
    ElMessage.warning('请先登录后再发起求助')
    return router.push('/login')
  }

  try {
    // 1. 调用后端接口获取实名认证详情
    const res = await request.get('/user-auth/detail')

    // 如果返回 code 200 但 data 为空，说明数据库 user_auth 表里还没这条记录
    if (res.code === "200") {
      const authData = res.data

      if (!authData) {
        // 情况 A: 没记录 -> 未认证
        showAuthConfirm('发起求助需先完成实名认证')
      } else if (authData.authStatus === 1) {
        // 情况 B: 状态为 1 -> 已通过
        router.push('/publish')
      } else if (authData.authStatus === 0) {
        // 情况 C: 状态为 0 -> 审核中
        ElMessageBox.alert('您的实名认证正在审核中，请稍后再试。', '提醒')
      } else if (authData.authStatus === 2) {
        // 情况 D: 状态为 2 -> 被驳回
        showAuthConfirm(`实名认证被驳回：${authData.rejectReason || '信息不符'}`)
      }
    } else {
      ElMessage.error(res.message || '获取认证信息失败')
    }
  } catch (err) {
    ElMessage.error('权限检查失败')
  }
}

// 引导认证的公共弹窗
const showAuthConfirm = (title) => {
  ElMessageBox.confirm(title, '权限提醒', {
    confirmButtonText: '去认证',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push('/user-auth')
  })
}

const logout = () => {
  sessionStorage.clear()
  router.replace('/login')
}

onMounted(fetchUser)
</script>

<style scoped>
.header {
  display: flex; align-items: center; background: rgba(255,255,255,0.98);
  height: 70px; padding: 0 60px; position: sticky; top: 0; z-index: 1000;
  box-shadow: 0 2px 15px rgba(0,0,0,0.05);
}
.logo { display: flex; align-items: center; margin-right: 40px; cursor: pointer; }
.logo-text { font-size: 22px; font-weight: 800; color: #f56c6c; }
.menu { flex: 1; border: none !important; }
.flex-grow { flex-grow: 1; }
.header-right { display: flex; align-items: center; gap: 20px; }
.page-container { padding: 0; background-color: #f8fafc; min-height: calc(100vh - 70px); }
.user-info { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.username { font-weight: 500; }
</style>