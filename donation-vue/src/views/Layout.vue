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

const handlePublish = () => {
  // 这里放入你之前的权限校验逻辑
  router.push('/publish')
}

const logout = () => {
  sessionStorage.clear()
  router.replace('/login')
}

onMounted(fetchUser)
</script>

<style scoped>
/* 这里的样式直接复用你之前 Home.vue 里的 .header 相关样式 */
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
.page-container { padding: 0; background-color: #f8fafc; }
.user-info { display: flex; align-items: center; gap: 10px; cursor: pointer; }
</style>