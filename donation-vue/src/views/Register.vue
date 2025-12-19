<template>
  <div class="register-container">
    <div class="background-image"></div>
    <div class="background-overlay"></div>

    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <div class="heart-icon">❤️</div>
          <h2 class="register-title">加入公益大家庭</h2>
          <p class="register-subtitle">开启您的爱心之旅，让每一份温暖都有迹可循</p>
        </div>
      </template>

      <el-form
          :model="regForm"
          :rules="rules"
          ref="regFormRef"
          label-position="top"
          size="large"
          class="modern-form"
      >
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="regForm.nickName" placeholder="怎么称呼您？" :prefix-icon="User" />
        </el-form-item>

        <div class="form-row">
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="regForm.mobile" placeholder="登录账号" :prefix-icon="Iphone" />
          </el-form-item>
        </div>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="regForm.email" placeholder="接收捐赠回执与通知" :prefix-icon="Message" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="regForm.password"
              type="password"
              placeholder="至少 6 位安全密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-button
            type="danger"
            @click="handleRegister"
            :loading="loading"
            class="submit-btn glow-btn"
        >
          立即注册
        </el-button>

        <div class="register-footer">
          <span>已经是志愿者了？</span>
          <el-link type="danger" :underline="false" @click="goToLogin">直接登录</el-link>
        </div>
      </el-form>

      <div class="trust-footer">
        <el-icon><Checked /></el-icon>
        <span>已通过安全合规性备案，您的隐私受保护</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Message, Checked } from '@element-plus/icons-vue'
import request from '../utils/request.js'

const router = useRouter()
const regFormRef = ref(null)
const loading = ref(false)

const regForm = reactive({
  nickName: '',
  mobile: '',
  email: '',
  password: ''
})

const rules = {
  nickName: [{ required: true, message: '取一个好听的名字吧', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '为了安全，密码不能少于 6 位', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!regFormRef.value) return

  await regFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user/register', regForm)
        ElMessage.success('欢迎加入！请使用刚刚注册的账号登录')
        router.push('/login')
      } catch (err) {
        ElMessage.error(err.response?.data?.message || '由于网络原因注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => router.push('/login')
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  padding: 40px 20px;
}

/* 统一背景图逻辑 */
.background-image {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: url('https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=2070');
  background-size: cover;
  background-position: center;
  z-index: -2;
  transform: scale(1.05); /* 默认微放，与登录页呼应 */
}

.background-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.4) 0%, rgba(30, 41, 59, 0.7) 100%);
  z-index: -1;
}

.register-card {
  width: 100%;
  max-width: 480px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  padding: 5px;
}

.register-header {
  text-align: center;
}

.heart-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.register-title {
  margin: 0;
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.register-subtitle {
  margin: 10px 0 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

/* 表单细节微调 */
:deep(.el-form-item__label) {
  font-weight: bold;
  color: #475569;
  padding-bottom: 4px;
}

.submit-btn {
  width: 100%;
  height: 50px;
  margin-top: 20px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 12px;
  transition: 0.3s;
}

.glow-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 15px rgba(245, 108, 108, 0.3);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #64748b;
}

.register-footer .el-link {
  font-weight: bold;
  margin-left: 5px;
}

.trust-footer {
  margin-top: 25px;
  border-top: 1px solid #f1f5f9;
  padding-top: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #10b981;
  font-size: 12px;
}

/* 适配移动端：去除卡片阴影让其更简洁 */
@media (max-width: 480px) {
  .register-card {
    background: rgba(255, 255, 255, 0.9);
    padding: 0;
  }
}
</style>