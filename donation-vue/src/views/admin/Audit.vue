<template>
  <div class="audit-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="title">项目审批中心</span>
          <div class="filter-area">
            <el-radio-group v-model="statusFilter" @change="loadData" size="default">
              <el-radio-button :label="0">待审核</el-radio-button>
              <el-radio-button :label="1">已通过</el-radio-button>
              <el-radio-button :label="2">已驳回</el-radio-button>
            </el-radio-group>
            <el-button icon="Refresh" circle @click="loadData" style="margin-left: 15px" />
          </div>
        </div>
      </template>

      <el-table :data="auditList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column prop="title" label="项目名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="userName" label="发起人" width="120" />
        <el-table-column prop="targetAmount" label="目标金额" width="150">
          <template #default="scope">
            <span class="money">¥{{ scope.row.targetAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusMap[scope.row.status].type">
              {{ statusMap[scope.row.status].text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-button
                type="primary"
                plain
                size="small"
                @click="openAudit(scope.row)"
                :disabled="scope.row.status !== 0"
            >
              去审核
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="项目详情审计" width="700px" destroy-on-close>
      <div class="audit-detail" v-if="currentProject">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="项目标题">{{ currentProject.title }}</el-descriptions-item>
          <el-descriptions-item label="求助说明">{{ currentProject.summary }}</el-descriptions-item>
          <el-descriptions-item label="证明材料">
            <div class="proof-gallery">
              <el-image
                  v-for="(img, index) in proofImages"
                  :key="index"
                  :src="img"
                  :preview-src-list="proofImages"
                  class="proof-img"
              />
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <div class="audit-form">
          <p class="form-label">审核结论：</p>
          <el-radio-group v-model="auditResult.status">
            <el-radio :label="1">通过 (准予发布)</el-radio>
            <el-radio :label="2">驳回 (填写原因)</el-radio>
          </el-radio-group>

          <div v-if="auditResult.status === 2" class="reason-input">
            <p class="form-label">驳回原因：</p>
            <el-input
                v-model="auditResult.reason"
                type="textarea"
                rows="3"
                placeholder="请输入具体驳回理由，用户可见"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="submitLoading">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const statusFilter = ref(0)
const auditList = ref([])
const currentProject = ref(null)

// 审核表单
const auditResult = ref({
  status: 1,
  reason: ''
})

// 状态映射表
const statusMap = {
  0: { text: '待审核', type: 'info' },
  1: { text: '进行中', type: 'success' },
  2: { text: '已驳回', type: 'danger' }
}

// 模拟证明图片（实际开发中从 currentProject 获取）
const proofImages = [
  'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=300',
  'https://images.unsplash.com/photo-1581595224492-38ff0376646b?w=300'
]

// 获取列表数据
const loadData = async () => {
  loading.value = true
  try {
    // 假设后端接口为 /admin/project/list，根据状态查询
    const res = await request.get('/admin/project/list', {
      params: { status: statusFilter.value }
    })
    if (res.code === "200") {
      auditList.value = res.data
    }
  } catch (err) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 打开审核弹窗
const openAudit = (row) => {
  currentProject.value = row
  auditResult.value = { status: 1, reason: '' }
  dialogVisible.value = true
}

// 提交审核结果
const submitAudit = async () => {
  if (auditResult.value.status === 2 && !auditResult.value.reason) {
    return ElMessage.warning('请填写驳回原因')
  }

  submitLoading.value = true
  try {
    const res = await request.post('/admin/project/audit', {
      projectId: currentProject.value.id,
      status: auditResult.value.status,
      reason: auditResult.value.reason
    })
    if (res.code === "200") {
      ElMessage.success('审核处理成功')
      dialogVisible.value = false
      loadData() // 刷新列表
    }
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.audit-container { padding: 20px; background: #f0f2f5; min-height: calc(100vh - 60px); }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.title { font-size: 18px; font-weight: bold; color: #303133; }
.money { color: #f56c6c; font-weight: bold; }

.proof-gallery { display: flex; gap: 10px; margin-top: 10px; }
.proof-img { width: 120px; height: 120px; border-radius: 4px; border: 1px solid #eee; cursor: pointer; }

.audit-form { margin-top: 25px; padding-top: 20px; border-top: 1px solid #eee; }
.form-label { font-weight: bold; margin-bottom: 12px; color: #606266; }
.reason-input { margin-top: 20px; }
</style>