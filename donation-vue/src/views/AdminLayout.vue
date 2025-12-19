<template>
  <div class="admin-layout">
    <el-container style="height: 100vh;">
      <el-aside width="220px" class="admin-aside">
        <div class="admin-logo">后台管理系统</div>
        <el-menu
            router
            active-text-color="#ffd04b"
            background-color="#304156"
            text-color="#fff"
            :default-active="route.path"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>管理概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/audit">
            <el-icon><Check /></el-icon>
            <span>项目审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="admin-header">
          <div class="header-left">欢迎回来，管理员</div>
          <el-button link @click="goHome">回到前台首页</el-button>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
// 1. 必须要引入这些 Vue Router 的钩子
import { useRoute, useRouter } from 'vue-router'
// 2. 必须要引入图标组件（如果你安装了 @element-plus/icons-vue）
import { DataBoard, Check, User } from '@element-plus/icons-vue'

// 3. 定义 route 变量，解决模板中 route.path 报错问题
const route = useRoute()
const router = useRouter()

// 4. 定义回到首页的方法
const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
.admin-aside { background-color: #304156; color: #fff; }
.admin-logo { height: 60px; line-height: 60px; text-align: center; font-weight: bold; background: #2b2f3a; color: #fff; }
.admin-header { border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; background-color: #fff; }
</style>