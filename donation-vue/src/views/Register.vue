<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2 class="register-title">❤️ 加入公益大家庭</h2>
          <p class="register-subtitle">您的每一个小举动，都在改变世界</p>
        </div>
      </template>

      <el-form
          :model="regForm"
          :rules="rules"
          ref="regFormRef"
          label-position="top"
          size="large"
      >
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="regForm.nickName" placeholder="怎么称呼你？" :prefix-icon="User" />
        </el-form-item>

        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="regForm.mobile" placeholder="用于登录和找回密码" :prefix-icon="Iphone" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="regForm.email" placeholder="接收系统通知" :prefix-icon="Message" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="regForm.password"
              type="password"
              placeholder="至少 6 位字符"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-button
            type="primary"
            @click="handleRegister"
            :loading="loading"
            class="submit-btn"
        >
          立即加入
        </el-button>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin">去登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Message } from '@element-plus/icons-vue'
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
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!regFormRef.value) return

  await regFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user/register', regForm)
        ElMessage.success(res.data || '注册成功！')
        router.push('/login')
      } catch (err) {
        ElMessage.error(err.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 统一的渐变背景 */
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 450px;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  padding: 10px 0;
}

.register-title {
  margin: 0;
  font-size: 22px;
  color: #409eff;
  font-weight: bold;
}

.register-subtitle {
  margin: 10px 0 0;
  font-size: 14px;
  color: #909399;
}

.submit-btn {
  width: 100%;
  height: 45px;
  margin-top: 10px;
  font-size: 16px;
  border-radius: 8px;
}

.register-footer {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
  color: #606266;
}

/* 移动端适配 */
@media (max-width: 480px) {
  .register-card {
    border: none;
    box-shadow: none;
    background: transparent;
  }
}
</style>