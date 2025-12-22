<template>
  <div class="update-page">
    <div class="page-banner">
      <div class="banner-content">
        <el-page-header @back="router.back()" title="返回">
          <template #content>
            <span class="header-title">发布公示 · 传递透明</span>
          </template>
        </el-page-header>
        <p class="banner-desc">您的每一次同步，都是对捐助者信任的最好回馈</p>
      </div>
    </div>

    <div class="main-container">
      <el-card class="update-card" shadow="hover">
        <div class="project-tag">
          <el-icon><CollectionTag /></el-icon>
          <span class="tag-label">当前项目：</span>{{ projectTitle }}
        </div>

        <el-form :model="form" label-position="top" ref="formRef" class="modern-form">

          <el-form-item label="本次更新性质" required>
            <el-radio-group v-model="form.updateType" class="custom-radio-group">
              <el-radio :label="1" border class="type-radio">
                <el-icon><Flag /></el-icon> 进展说明
              </el-radio>
              <el-radio :label="2" border class="type-radio">
                <el-icon><Wallet /></el-icon> 用款说明
              </el-radio>
              <el-radio :label="3" border class="type-radio">
                <el-icon><CircleCheck /></el-icon> 结项总结
              </el-radio>
            </el-radio-group>

            <transition name="el-fade-in">
              <div class="type-info-box">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ typeTips[form.updateType] }}</span>
              </div>
            </transition>
          </el-form-item>

          <el-divider />

          <el-form-item label="详细情况公示" required>
            <el-input
                v-model="form.content"
                type="textarea"
                :rows="10"
                maxlength="1000"
                show-word-limit
                placeholder="请如实记录受助人的现状变化、治疗进度或善款支出的具体明细..."
                class="content-textarea"
            />
          </el-form-item>

          <el-form-item label="证明材料 / 现场照片">
            <div class="upload-wrapper">
              <el-upload
                  action="http://localhost:8080/file/upload"
                  list-type="picture-card"
                  :headers="uploadHeaders"
                  :on-success="handleUploadSuccess"
                  :on-remove="handleRemove"
                  :file-list="fileList"
                  multiple
                  accept=".jpg,.jpeg,.png"
              >
                <div class="upload-slot">
                  <el-icon><CameraFilled /></el-icon>
                  <span>添加图片</span>
                </div>
              </el-upload>
            </div>
            <div class="upload-help">
              <p><el-icon><InfoFilled /></el-icon> 图片支持 JPG/PNG 格式，单张不超过 5MB</p>
              <p><el-icon><Checked /></el-icon> 建议上传：受助人近况、医院发票、购买凭证或执行现场图</p>
            </div>
          </el-form-item>

          <div class="submit-area">
            <el-button size="large" round @click="router.back()" class="btn-cancel">取消</el-button>
            <el-button
                type="primary"
                size="large"
                round
                :loading="submitting"
                @click="submitUpdate"
                class="btn-submit"
            >
              签署并发布公示
            </el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  CameraFilled, CollectionTag, Flag, Wallet,
  CircleCheck, InfoFilled, Checked
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 基础数据初始化
const projectTitle = ref(route.query.title || '载入中...')
const projectId = route.query.projectId
const submitting = ref(false)

const fileList = ref([])      // 用于 el-upload 内部显示的数组
const imageUrls = ref([])    // 用于存储返回的纯图片地址

const form = ref({
  projectId: projectId,
  updateType: 1,
  content: '',
  images: '' // 最终提交给后端的逗号分隔字符串
})

const typeTips = {
  1: '主要用于更新受助人最新的健康状况、学习进度或生活现状变化。',
  2: '主要用于公示本阶段善款的具体去向，如药费单据、物资采购合同等。',
  3: '项目已执行完毕，向所有捐款人汇报整体成果、社会效益及剩余款项处理。'
}

const uploadHeaders = {
  Authorization: `Bearer ${sessionStorage.getItem('token')}`
}

// 图片上传逻辑
const handleUploadSuccess = (res) => {
  if (res.code === "200") {
    imageUrls.value.push(res.data)
  } else {
    ElMessage.error(res.message || '图片上传失败')
  }
}

const handleRemove = (file) => {
  // 提取需要移除的URL
  const targetUrl = file.response ? file.response.data : file.url
  imageUrls.value = imageUrls.value.filter(url => url !== targetUrl)
}

// 提交公示
const submitUpdate = async () => {
  if (!form.value.content || form.value.content.length < 10) {
    return ElMessage.warning('公示内容说明不能少于10个字')
  }

  // 数组转逗号分隔字符串
  form.value.images = imageUrls.value.join(',')

  submitting.value = true
  try {
    // 对应你的后端的控制器路径：ProjectsUpdateController
    const res = await request.post('/projectUpdate/save', form.value)
    if (res.code === "200") {
      ElMessage({
        message: '发布成功！您的透明度公示已生效',
        type: 'success',
        duration: 2000
      })
      router.push('/profile')
    }
  } catch (err) {
    console.error('Submit Error:', err)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.update-page { background-color: #f4f7f6; min-height: 100vh; padding-bottom: 60px; }

/* 顶部 Banner 样式 */
.page-banner {
  background: linear-gradient(135deg, #42b983 0%, #35495e 100%);
  height: 220px;
  color: white;
  padding: 40px 18% 0;
}
.header-title { color: white; font-weight: 700; font-size: 20px; }
.banner-desc { opacity: 0.8; margin-top: 10px; font-size: 14px; letter-spacing: 1px; }

/* 容器布局 */
.main-container { margin-top: -80px; padding: 0 18%; }

/* 卡片样式优化 */
.update-card {
  border-radius: 20px;
  border: none;
  box-shadow: 0 12px 40px rgba(0,0,0,0.06);
  padding: 24px;
}

.project-tag {
  background: #f0fdf4;
  color: #166534;
  padding: 12px 20px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  margin-bottom: 30px;
}
.tag-label { opacity: 0.7; font-weight: 400; font-size: 13px; }

/* 类型选择器 */
.custom-radio-group { display: flex; gap: 15px; flex-wrap: wrap; }
.type-radio {
  margin: 0 !important;
  height: auto;
  padding: 12px 24px !important;
  border-radius: 14px !important;
  transition: all 0.3s ease;
}
.type-radio.is-checked {
  background: #f0fdf4;
  border-color: #42b983;
  transform: translateY(-2px);
}

.type-info-box {
  margin-top: 15px;
  background: #f8fafc;
  padding: 14px 18px;
  border-left: 5px solid #42b983;
  color: #64748b;
  font-size: 13.5px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 0 8px 8px 0;
}

/* 文本域优化 */
.content-textarea :deep(.el-textarea__inner) {
  border-radius: 14px;
  padding: 18px;
  background: #fcfdfd;
  border: 1px solid #e2e8f0;
  font-size: 15px;
  line-height: 1.6;
}
.content-textarea :deep(.el-textarea__inner:focus) {
  background: white;
  border-color: #42b983;
  box-shadow: 0 0 0 4px rgba(66, 185, 131, 0.08);
}

/* 上传控件样式 */
.upload-wrapper :deep(.el-upload--picture-card) {
  background-color: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 18px;
  width: 128px;
  height: 128px;
  transition: border-color 0.3s;
}
.upload-wrapper :deep(.el-upload--picture-card:hover) {
  border-color: #42b983;
}
.upload-slot {
  display: flex;
  flex-direction: column;
  color: #94a3b8;
  line-height: 1.2;
}
.upload-slot .el-icon { font-size: 28px; margin-bottom: 8px; }
.upload-slot span { font-size: 13px; }

.upload-help { margin-top: 18px; }
.upload-help p {
  font-size: 12.5px;
  color: #94a3b8;
  margin: 6px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 底部操作区 */
.submit-area {
  margin-top: 60px;
  display: flex;
  justify-content: center;
  gap: 24px;
  border-top: 1px solid #f1f5f9;
  padding-top: 30px;
}
.btn-submit {
  padding: 0 50px;
  background: #42b983;
  border: none;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(66, 185, 131, 0.3);
}
.btn-submit:hover { background: #3dae7b; transform: translateY(-2px); box-shadow: 0 6px 20px rgba(66, 185, 131, 0.4); }
.btn-cancel { padding: 0 35px; color: #64748b; }
</style>