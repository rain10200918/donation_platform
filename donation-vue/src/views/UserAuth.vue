<template>
  <div class="auth-page">
    <div class="auth-bg-ornament"></div>

    <div class="auth-wrapper">
      <el-card class="auth-main-card" shadow="always">
        <div class="auth-header">
          <div class="icon-shield">
            <el-icon :size="40" color="#67c23a"><CircleCheckFilled /></el-icon>
          </div>
          <h2>实名身份认证</h2>
          <p>为了保障公益项目的真实性，请完成实名认证</p>
        </div>

        <transition name="el-zoom-in-top">
          <div v-if="authInfo.authStatus !== null" class="status-indicator">
            <div :class="['status-ribbon', statusTag.type]">
              <el-icon><InfoFilled /></el-icon>
              <span>当前状态：{{ statusTag.text }}</span>
            </div>

            <el-alert
                v-if="authInfo.authStatus === 2"
                title="审核未通过"
                type="error"
                :description="'驳回原因：' + (authInfo.rejectReason || '提交的身份信息与公安系统不匹配')"
                show-icon
                :closable="false"
                style="margin-top: 15px"
            />
          </div>
        </transition>

        <el-form
            :model="authForm"
            :rules="rules"
            ref="authFormRef"
            label-position="top"
            :disabled="isReadOnly"
            class="modern-form"
        >
          <div class="form-group-box">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                  v-model="authForm.realName"
                  placeholder="请输入您的法定姓名"
                  size="large"
              >
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>

            <el-form-item label="身份证号" prop="idNumber">
              <el-input
                  v-model="authForm.idNumber"
                  placeholder="请输入18位身份证号码"
                  maxlength="18"
                  size="large"
                  show-word-limit
              >
                <template #prefix><el-icon><Postcard /></el-icon></template>
              </el-input>
            </el-form-item>
          </div>

          <div v-if="!isReadOnly" class="auth-footer">
            <el-button
                type="primary"
                @click="submitForm"
                :loading="loading"
                class="glow-button"
            >
              提交审核
            </el-button>
            <p class="privacy-note">点击提交即表示您同意平台调用接口核实信息真实性</p>
          </div>

          <div v-else class="readonly-footer">
            <div class="secure-badge" v-if="authInfo.authStatus === 1">
              <el-icon><Lock /></el-icon> 您的信息已加密，仅用于合规性验证
            </div>
            <el-button plain @click="router.back()">返回个人中心</el-button>
          </div>
        </el-form>
      </el-card>

      <div class="safety-footer">
        <div class="safety-item">
          <el-icon><Checked /></el-icon> 官方核验
        </div>
        <div class="safety-item">
          <el-icon><Lock /></el-icon> 隐私加密
        </div>
        <div class="safety-item">
          <el-icon><Coordinate /></el-icon> 法律保障
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  InfoFilled, User, Postcard, CircleCheckFilled,
  Lock, Checked, Coordinate
} from '@element-plus/icons-vue'
import request from '../utils/request.js'

const router = useRouter()
const authFormRef = ref(null)
const loading = ref(false)
const authInfo = ref({ authStatus: null, rejectReason: '' })

const authForm = reactive({
  realName: '',
  idNumber: ''
})

const isReadOnly = computed(() => authInfo.value.authStatus === 0 || authInfo.value.authStatus === 1)

const statusTag = computed(() => {
  const map = {
    0: { text: '审核中', type: 'warning' },
    1: { text: '已认证成功', type: 'success' },
    2: { text: '审核失败', type: 'danger' }
  }
  return map[authInfo.value.authStatus] || { text: '待申请', type: 'info' }
})

const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5]{2,10}$/, message: '请输入2-10位中文姓名', trigger: 'blur' }
  ],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证格式不正确', trigger: 'blur' }
  ]
}

const fetchAuthStatus = async () => {
  try {
    const res = await request.get('/user-auth/detail')
    if (res.code === "200" && res.data) {
      authInfo.value = res.data
      authForm.realName = res.data.realName
      authForm.idNumber = res.data.idNumber
    }
  } catch (err) {}
}

const submitForm = () => {
  authFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/user-auth/apply', authForm)
        if (res.code === "200") {
          ElMessage.success('申请提交成功')
          await fetchAuthStatus()
        } else {
          ElMessage.error(res.message || '申请失败')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(fetchAuthStatus)
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background-color: #f8fafc;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.auth-bg-ornament {
  position: absolute;
  width: 1000px;
  height: 1000px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.05) 0%, transparent 70%);
  top: -500px;
  right: -500px;
}

.auth-wrapper {
  width: 100%;
  max-width: 500px;
  z-index: 1;
}

.auth-main-card {
  border-radius: 20px;
  border: none;
  padding: 20px;
}

/* 头部样式 */
.auth-header {
  text-align: center;
  margin-bottom: 30px;
}
.icon-shield {
  margin-bottom: 15px;
}
.auth-header h2 {
  margin: 0;
  font-size: 24px;
  color: #1e293b;
}
.auth-header p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 14px;
}

/* 状态条 */
.status-indicator {
  margin-bottom: 25px;
}
.status-ribbon {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: bold;
}
.status-ribbon.warning { background: #fffbeb; color: #d97706; }
.status-ribbon.success { background: #f0fdf4; color: #16a34a; }
.status-ribbon.danger { background: #fef2f2; color: #dc2626; }

/* 表单框 */
.form-group-box {
  background: #f8fafc;
  padding: 25px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.auth-footer {
  margin-top: 30px;
  text-align: center;
}

.glow-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.privacy-note {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 15px;
}

.readonly-footer {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.secure-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #10b981;
  background: #ecfdf5;
  padding: 10px;
  border-radius: 8px;
}

/* 安全底部 */
.safety-footer {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 40px;
}
.safety-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #94a3b8;
}

:deep(.el-input__wrapper) {
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  background: #fff;
}
</style>