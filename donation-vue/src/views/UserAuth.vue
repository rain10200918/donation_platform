<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <div class="card-header">
          <span class="title">实名认证</span>
          <el-tag :type="statusTag.type" effect="dark">{{ statusTag.text }}</el-tag>
        </div>
      </template>

      <el-alert
          v-if="authInfo.authStatus === 2"
          title="认证未通过"
          type="danger"
          :description="'驳回原因：' + (authInfo.rejectReason || '信息不符')"
          show-icon
          style="margin-bottom: 20px"
      />

      <div v-if="isReadOnly" class="status-banner" :class="statusTag.type">
        <el-icon><InfoFilled /></el-icon>
        <span v-if="authInfo.authStatus === 0">您的实名信息正在审核中，请耐心等待。</span>
        <span v-if="authInfo.authStatus === 1">您已完成实名认证，信息已加密保护。</span>
      </div>

      <el-form
          :model="authForm"
          :rules="rules"
          ref="authFormRef"
          label-width="100px"
          label-position="top"
          :disabled="isReadOnly"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input
              v-model="authForm.realName"
              placeholder="请输入您的法定姓名"
              prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="身份证号" prop="idNumber">
          <el-input
              v-model="authForm.idNumber"
              placeholder="请输入18位身份证号码"
              maxlength="18"
              show-word-limit
              prefix-icon="Postcard"
          />
        </el-form-item>

        <el-form-item v-if="!isReadOnly" class="form-actions">
          <el-button type="primary" @click="submitForm" :loading="loading" class="submit-btn">
            提交认证申请
          </el-button>
        </el-form-item>
      </el-form>

      <div class="security-tips">
        <p>🛡️ 您的隐私信息将被严格保密，仅用于公益项目合规性审核。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled, User, Postcard } from '@element-plus/icons-vue'
import request from '../utils/request.js'

const authFormRef = ref(null)
const loading = ref(false)
const authInfo = ref({ authStatus: null, rejectReason: '' })

// 1. 表单数据模型
const authForm = reactive({
  realName: '',
  idNumber: ''
})

// 2. 状态控制逻辑
const isReadOnly = computed(() => {
  // 0:审核中, 1:通过 -> 此时表单锁定不可改
  return authInfo.value.authStatus === 0 || authInfo.value.authStatus === 1
})

const statusTag = computed(() => {
  const map = {
    0: { text: '审核中', type: 'warning' },
    1: { text: '已认证', type: 'success' },
    2: { text: '已驳回', type: 'danger' }
  }
  return map[authInfo.value.authStatus] || { text: '未认证', type: 'info' }
})

// 3. 校验规则 (对应后端的 IdCardUtil 校验)
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

// 4. 获取初始化状态
const fetchAuthStatus = async () => {
  try {
    const res = await request.get('/user-auth/detail') // 路径与后端RequestMapping对齐
    if (res.code === "200" && res.data) {
      authInfo.value = res.data
      authForm.realName = res.data.realName
      authForm.idNumber = res.data.idNumber
    }
  } catch (err) {
    console.error('获取认证信息失败', err)
  }
}

// 5. 提交申请
const submitForm = () => {
  authFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 使用 @RequestBody 方式发送 JSON 对象
        const res = await request.post('/user-auth/apply', authForm)

        // 适配“不剥壳”模式
        if (res.code === "200") {
          ElMessage.success(res.data || '申请提交成功')
          await fetchAuthStatus() // 重新拉取状态，锁定表单
        } else {
          ElMessage.error(res.message || '申请失败')
        }
      } catch (err) {
        ElMessage.error(err.response?.data?.message || '网络请求异常')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchAuthStatus()
})
</script>

<style scoped>
.auth-container {
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 40px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.status-banner {
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.status-banner.warning { background-color: #fdf6ec; color: #e6a23c; border: 1px solid #faecd8; }
.status-banner.success { background-color: #f0f9eb; color: #67c23a; border: 1px solid #e1f3d8; }

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
}

.security-tips {
  margin-top: 30px;
  text-align: center;
  font-size: 12px;
  color: #909399;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.form-actions {
  margin-top: 30px;
}
</style>