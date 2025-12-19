<template>
  <div class="admin-layout">
    <el-container class="layout-container">
      <el-aside :width="isCollapse ? '64px' : '240px'" class="aside-container">
        <div class="logo-box">
          <el-icon size="24" color="#409eff"><Box /></el-icon>
          <span v-show="!isCollapse" class="logo-text">爱心筹管理后台</span>
        </div>

        <el-scrollbar>
          <el-menu
              :default-active="route.path"
              router
              :collapse="isCollapse"
              background-color="#304156"
              text-color="#bfcbd9"
              active-text-color="#409eff"
              :collapse-transition="false"
              class="side-menu"
          >
            <el-menu-item index="/admin/dashboard">
              <el-icon><DataLine /></el-icon>
              <template #title>管理概览</template>
            </el-menu-item>

            <el-menu-item index="/admin/audit">
              <el-icon><Checked /></el-icon>
              <template #title>项目审批</template>
            </el-menu-item>

            <el-menu-item index="/admin/users">
              <el-icon><UserFilled /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>

            <el-menu-item index="/admin/settings">
              <el-icon><Setting /></el-icon>
              <template #title>系统设置</template>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <el-container>
        <el-header class="header-container">
          <div class="header-left">
            <el-icon class="collapse-icon" @click="isCollapse = !isCollapse">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>

            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="route.path !== '/admin/dashboard'">
                {{ currentMenuName }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <el-tooltip content="回到门户首页" placement="bottom">
              <div class="action-item" @click="goHome">
                <el-icon><Monitor /></el-icon>
              </div>
            </el-tooltip>

            <el-dropdown trigger="click" class="user-dropdown">
              <div class="avatar-wrapper">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <span class="user-name">系统管理员</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item icon="User">个人中心</el-dropdown-item>
                  <el-dropdown-item divided icon="SwitchButton" @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-body">
          <div class="content-view">
            <router-view v-slot="{ Component }">
              <transition name="fade-transform" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Box, DataLine, Checked, UserFilled, Setting,
  Expand, Fold, Monitor, ArrowDown, User, SwitchButton
} from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

// 动态计算面包屑
const currentMenuName = computed(() => {
  const menuMap = {
    '/admin/dashboard': '管理概览',
    '/admin/audit': '项目审批',
    '/admin/users': '用户管理',
    '/admin/settings': '系统设置'
  }
  return menuMap[route.path] || '业务操作'
})

const goHome = () => router.push('/home')

const handleLogout = () => {
  ElMessageBox.confirm('确认退出管理系统吗？', '提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里清除 token
    localStorage.removeItem('admin_token')
    router.push('/login')
    ElMessage.success('已安全退出')
  })
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
  background-color: #f0f2f5;
}

.layout-container {
  height: 100%;
}

/* 侧边栏样式 */
.aside-container {
  background-color: #304156;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 100;
}

.logo-box {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: #2b2f3a;
  color: #fff;
  overflow: hidden;
}

.logo-text {
  font-weight: 600;
  font-size: 16px;
  white-space: nowrap;
}

.side-menu {
  border-right: none;
}

/* 顶栏样式 */
.header-container {
  background: #fff;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.collapse-icon:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-item {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
  font-size: 18px;
  color: #606266;
}

.action-item:hover {
  background: #f6f6f6;
}

.user-dropdown {
  margin-left: 8px;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.avatar-wrapper:hover {
  background: #f6f6f6;
}

.user-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 内容区 */
.main-body {
  padding: 0;
  background-color: #f0f2f5;
}

.content-view {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>