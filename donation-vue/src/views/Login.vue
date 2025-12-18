<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2 class="login-title">❤️ 公益众筹平台</h2>
          <p class="login-subtitle">传递爱心，温暖每一个角落</p>
        </div>
      </template>

      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large">
        <el-form-item prop="mobile">
          <el-input
              v-model="loginForm.mobile"
              placeholder="请输入手机号"
              :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              @click="handleLogin"
              :loading="loading"
              class="login-btn"
          >
            登 录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../utils/request.js'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  mobile: '',
  password: ''
})

const rules = {
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user/login', loginForm)
        // 确保清除旧的再存入新的，防止冲突
        sessionStorage.removeItem('token')
        sessionStorage.setItem('token', res.data)

        ElMessage.success('登录成功，欢迎回来')
        await router.push('/home')
      } catch (err) {
        ElMessage.error(err.response?.data?.message || '登录失败，请检查网络')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 使用与注册页/首页一致的渐变色 */
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 420px;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  padding: 10px 0;
}

.login-title {
  margin: 0;
  font-size: 24px;
  color: #409eff;
  font-weight: bold;
}

.login-subtitle {
  margin: 10px 0 0;
  font-size: 14px;
  color: #909399;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 10px;
}

.login-footer {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #606266;
}

/* 适配移动端 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    margin: 20px;
  }
}
</style>