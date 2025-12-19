<template>
  <div class="login-container">
    <div class="background-image"></div>
    <div class="background-overlay"></div>

    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <div class="heart-icon">❤️</div>
          <h2 class="login-title">公益众筹平台</h2>
          <p class="login-subtitle">每一份善心，都是改变世界的力量</p>
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
              type="danger"
              @click="handleLogin"
              :loading="loading"
              class="login-btn glow-btn"
          >
            开启爱心之旅
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>加入我们，成为传递者</span>
          <el-link type="danger" :underline="false" @click="router.push('/register')">立即注册</el-link>
        </div>
      </el-form>

      <div class="trust-badge">
        <el-icon><CircleCheck /></el-icon>
        <span>平台已受法律监管，确保资金安全透明</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'
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
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
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
        // 第一步：获取 token
        const res = await request.post('/user/login', loginForm)
        const token = res.data
        sessionStorage.setItem('token', token)

        // 第二步：拿着 token 去获取用户信息（包含 role）
        // 确保你的 request.js 拦截器已经配置好在请求头带上 token
        const userRes = await request.get('/user/info')

        if (userRes.code === "200") {
          const userInfo = userRes.data
          sessionStorage.setItem('userInfo', JSON.stringify(userInfo))

          ElMessage.success('登录成功，欢迎回来')

          // 第三步：根据角色分流
          if (userInfo.role == 1) {
            router.push('/admin/audit') // 管理员去后台
          } else {
            router.push('/home') // 普通用户去前台
          }
        }
      } catch (err) {
        console.error(err)
        ElMessage.error(err.response?.data?.message || '登录失败，请检查账号密码')
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
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 核心：背景图片 */
.background-image {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: url('https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=2070');
  background-size: cover;
  background-position: center;
  z-index: -2;
  transition: transform 10s ease-in-out;
}

/* 动态背景感：慢慢放大 */
.login-container:hover .background-image {
  transform: scale(1.1);
}

.background-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  /* 带有品牌色的半透明渐变，让文案更清晰 */
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.4) 0%, rgba(30, 41, 59, 0.7) 100%);
  z-index: -1;
}

.login-card {
  width: 420px;
  border-radius: 20px;
  /* 毛玻璃效果 */
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 10px;
}

.login-header {
  text-align: center;
  padding: 10px 0;
}

.heart-icon {
  font-size: 40px;
  margin-bottom: 10px;
  animation: heartbeat 1.5s infinite;
}

@keyframes heartbeat {
  0% { transform: scale(1); }
  14% { transform: scale(1.3); }
  28% { transform: scale(1); }
  42% { transform: scale(1.3); }
  70% { transform: scale(1); }
}

.login-title {
  margin: 0;
  font-size: 26px;
  color: #f56c6c; /* 使用温情的红色 */
  letter-spacing: 1px;
}

.login-subtitle {
  margin: 12px 0 0;
  font-size: 14px;
  color: #64748b;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 12px;
  margin-top: 15px;
  transition: 0.3s;
}

.glow-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 15px rgba(245, 108, 108, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #64748b;
}

.login-footer .el-link {
  margin-left: 8px;
  font-weight: bold;
}

.trust-badge {
  margin-top: 25px;
  border-top: 1px solid #f1f5f9;
  padding-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: #10b981;
  font-size: 12px;
}

/* 适配移动端 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
  }
}
</style>