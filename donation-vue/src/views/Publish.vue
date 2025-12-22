<template>
  <div class="publish-wrapper">
    <div class="bg-decorator"></div>

    <div class="publish-main">
      <el-row class="publish-card-group">
        <el-col :span="8" class="brand-sidebar">
          <div class="brand-content">
            <div class="logo-area">
              <el-icon :size="40" color="#fff"><Sunny /></el-icon>
              <h2>爱心互助平台</h2>
            </div>
            <div class="slogan">
              <h3>让每一份困苦，<br/>都能遇见温暖的微光。</h3>
              <p>已有 1,280 位求助者在这里获得了帮助。</p>
            </div>

            <ul class="steps-visual">
              <li class="active"><span class="dot"></span> 填写求助详情</li>
              <li><span class="dot"></span> 平台人工审核</li>
              <li><span class="dot"></span> 开启爱心募捐</li>
              <li><span class="dot"></span> 资金拨付使用</li>
            </ul>
          </div>
        </el-col>

        <el-col :span="16" class="form-section">
          <div class="form-container">
            <div class="form-header">
              <h2>发起求助申请</h2>
              <el-button link type="primary" @click="router.back()">返回上页</el-button>
            </div>

            <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
              <el-form-item label="你想为这个求助项目起什么名字？" prop="title">
                <el-input
                    v-model="form.title"
                    size="large"
                    placeholder="如：白血病少年小王的希望之路"
                    maxlength="50"
                    show-word-limit
                />
              </el-form-item>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="所属类别" prop="projectType">
                    <el-select v-model="form.projectType" placeholder="请选择" style="width: 100%" size="large">
                      <el-option label="🏥 个人大病求助" :value="1" />
                      <el-option label="📚 助学公益项目" :value="2" />
                      <el-option label="🌪️ 突发灾害救援" :value="3" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="需要的帮助金额 (元)" prop="targetAmount">
                    <el-input-number
                        v-model="form.targetAmount"
                        :precision="0"
                        :min="100"
                        controls-position="right"
                        style="width: 100%"
                        size="large"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="您希望募捐在何时结束？" prop="endTime">
                <el-date-picker
                    v-model="form.endTime"
                    type="datetime"
                    placeholder="选择截止日期"
                    style="width: 100%"
                    size="large"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :disabled-date="disabledDate"
                />
              </el-form-item>

              <el-form-item label="上传相关证明/项目封面 (第一张将作为封面)" prop="picture">
                <div class="upload-grid">
                  <el-upload
                      action="http://localhost:8080/file/upload"
                      :headers="uploadHeaders"
                      list-type="picture-card"
                      :on-success="handleUploadSuccess"
                      :on-remove="handleRemove"
                      :before-upload="beforeUpload"
                      :limit="3"
                      name="file"
                      accept=".jpg,.jpeg,.png"
                  >
                    <el-icon><Plus /></el-icon>
                    <template #tip>
                      <div class="el-upload__tip">
                        请上传医疗证明、身份核实文件或生活照，最多3张。
                      </div>
                    </template>
                  </el-upload>
                </div>
              </el-form-item>

              <el-form-item label="讲述您的故事" prop="summary">
                <el-input
                    v-model="form.summary"
                    type="textarea"
                    :rows="6"
                    placeholder="真诚地描述您的困难，越具体的细节越能打动人心。建议包含：目前的困难、家庭状况、预期的资金用途..."
                    maxlength="1000"
                    show-word-limit
                />
              </el-form-item>

              <div class="warm-tip-card">
                <el-icon color="#f56c6c"><InfoFilled /></el-icon>
                <span>您的信息提交后，平台志愿者将在 24 小时内联系您确认详情。</span>
              </div>

              <div class="form-footer">
                <el-checkbox v-model="agree" class="agree-checkbox">
                  我承诺信息真实，愿对内容的真实性负责
                </el-checkbox>
                <el-button type="danger" size="large" @click="submitForm" :loading="loading" :disabled="!agree" class="submit-btn-premium">
                  开启爱心通道
                </el-button>
              </div>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive ,computed} from 'vue'
import { useRouter } from 'vue-router'
import { Sunny, InfoFilled,Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request.js'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const agree = ref(false)

const form = reactive({
  title: '',
  projectType: '',
  targetAmount: 0,
  summary: '',
  endTime: '',
  picture: ''
})

const rules = {
  title: [{ required: true, message: '请给项目起一个好听的名字', trigger: 'blur' }],
  projectType: [{ required: true, message: '请选择求助类型', trigger: 'change' }],
  targetAmount: [{ required: true, message: '请填写目标金额', trigger: 'blur' }],
  summary: [{ required: true, message: '请写下您的故事', trigger: 'blur' }],
  endTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
  picture : [{ required: true, message: '请至少上传一张证明图片', trigger: 'change' }]
}
const uploadHeaders = computed(() => {
  const token = sessionStorage.getItem('token');
  return token ? {
    'Authorization': `Bearer ${token}`
  } : {};
});

const handleUploadSuccess = (res) => {
  if (res.code === "200") {
    // 如果是单图，直接赋值
    form.picture = res.data
    // 如果需要多图（配合后端），可以用数组 push 再 join(',')
    ElMessage.success('图片上传成功')
  }
}
const handleRemove = () => {
  form.picture = ''
  ElMessage.info('图片已移除')
}

const beforeUpload = (file) => {
  const isImg = ['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImg) ElMessage.error('只能上传图片格式!')
  if (!isLt5M) ElMessage.error('图片大小不能超过 5MB!')
  return isImg && isLt5M
}
const disabledDate = (time) => time.getTime() < Date.now()

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/project/publish', form)
        if (res.code === "200") {
          ElMessage({
            message: '提交成功，爱心已开启！',
            type: 'success',
            duration: 5000
          })
          router.push('/my-projects')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.publish-wrapper {
  min-height: 100vh;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  position: relative;
  overflow: hidden;
}

.bg-decorator {
  position: absolute;
  top: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(245,108,108,0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.publish-main {
  width: 950px;
  z-index: 1;
}

.publish-card-group {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
}

/* 左侧边栏：采用深色渐变吸引眼球 */
.brand-sidebar {
  background: linear-gradient(180deg, #f56c6c 0%, #ff8e8e 100%);
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
}

.logo-area { display: flex; align-items: center; gap: 10px; margin-bottom: 50px; }
.logo-area h2 { font-size: 20px; margin: 0; letter-spacing: 1px; }

.slogan h3 { font-size: 26px; line-height: 1.4; margin-bottom: 15px; }
.slogan p { font-size: 14px; opacity: 0.8; }

.steps-visual {
  margin-top: auto;
  list-style: none;
  padding: 0;
}
.steps-visual li {
  margin-bottom: 20px;
  opacity: 0.6;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
}
.steps-visual li.active { opacity: 1; font-weight: bold; }
.dot { width: 8px; height: 8px; background: white; border-radius: 50%; }

/* 右侧表单区 */
.form-section { padding: 40px 50px; }
.form-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
.form-header h2 { margin: 0; color: #303133; font-size: 22px; }

.warm-tip-card {
  background: #fef2f2;
  border: 1px solid #fee2e2;
  padding: 12px 15px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #991b1b;
  margin: 20px 0;
}

.form-footer {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.agree-checkbox { margin-bottom: 0; }

.submit-btn-premium {
  height: 50px;
  font-size: 18px;
  font-weight: bold;
  border-radius: 10px;
  background: #f56c6c;
  border: none;
  box-shadow: 0 4px 14px 0 rgba(245,108,108,0.39);
  transition: all 0.3s;
}
.submit-btn-premium:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245,108,108,0.5);
}

:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  background-color: #f9fafb;
  box-shadow: none !important;
  border: 1px solid #e5e7eb;
}
:deep(.el-input__wrapper.is-focus), :deep(.el-textarea__inner:focus) {
  border-color: #f56c6c;
}
/* 图片上传区域微调 */
.upload-grid {
  margin-top: 8px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
  border-radius: 12px;
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  background-color: #f9fafb;
  border: 1px dashed #e5e7eb;
}

:deep(.el-upload--picture-card:hover) {
  border-color: #f56c6c;
  color: #f56c6c;
}
</style>