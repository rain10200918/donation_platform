<template>
  <div class="audit-container">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="title">项目审批中心</span>
          <div class="filter-area">
            <el-radio-group v-model="auditStatusFilter" @change="handleStatusChange" size="default">
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
        <el-table-column prop="title" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="targetAmount" label="目标金额" width="120">
          <template #default="scope">
            <span class="money">¥{{ scope.row.targetAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100">
          <template #default="scope">
            <el-tag :type="auditStatusMap[scope.row.auditStatus]?.type">
              {{ auditStatusMap[scope.row.auditStatus]?.text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="scope">
            <el-button
                v-if="scope.row.auditStatus === 0"
                type="primary"
                size="small"
                @click="openAudit(scope.row)"
            >
              去审核
            </el-button>
            <el-button
                v-else
                type="info"
                plain
                size="small"
                @click="openAudit(scope.row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="currentProject?.auditStatus === 0 ? '项目审核' : '项目详情'"
        width="700px"
        destroy-on-close
    >
      <div class="audit-detail" v-if="currentProject">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="项目标题">{{ currentProject.title }}</el-descriptions-item>
          <el-descriptions-item label="求助说明">{{ currentProject.summary }}</el-descriptions-item>
          <el-descriptions-item label="目标筹款">¥ {{ currentProject.targetAmount }}</el-descriptions-item>
        </el-descriptions>

        <div class="audit-result-section">
          <div v-if="currentProject.auditStatus === 0" class="audit-form">
            <p class="form-label">审核结论：</p>
            <el-radio-group v-model="auditForm.status">
              <el-radio :label="1">通过 (准予发布)</el-radio>
              <el-radio :label="2">驳回 (填写原因)</el-radio>
            </el-radio-group>

            <div v-if="auditForm.status === 2" class="reason-input">
              <p class="form-label">驳回原因：</p>
              <el-input
                  v-model="auditForm.reason"
                  type="textarea"
                  rows="3"
                  placeholder="请说明驳回理由，用户修改时可见"
              />
            </div>
          </div>

          <div v-else-if="currentProject.auditStatus === 1" class="status-tip">
            <el-alert title="项目已通过审核" type="success" description="该项目已进入募集阶段。" :closable="false" show-icon />
          </div>

          <div v-else-if="currentProject.auditStatus === 2" class="status-tip">
            <el-alert
                title="项目已被驳回"
                type="error"
                :description="'驳回理由：' + (currentProject.rejectReason || '未填写具体原因')"
                :closable="false"
                show-icon
            />
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">
          {{ currentProject?.auditStatus === 0 ? '取 消' : '关 闭' }}
        </el-button>
        <el-button
            v-if="currentProject?.auditStatus === 0"
            type="primary"
            @click="submitAudit"
            :loading="submitLoading"
        >
          确认提交
        </el-button>
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
const auditStatusFilter = ref(0) // 对应 audit_status: 0待审
const auditList = ref([])
const currentProject = ref(null)

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const auditForm = ref({
  status: 1,
  reason: ''
})

// 对应 SQL 表设计中的审核状态
const auditStatusMap = {
  0: { text: '待审核', type: 'info' },
  1: { text: '已通过', type: 'success' },
  2: { text: '已拒绝', type: 'danger' }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/project/list', {
      params: {
        status: auditStatusFilter.value, // 发送给后端的 status 参数对应数据库 audit_status
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    if (res.code === "200") {
      auditList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (err) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  pageNum.value = val
  loadData()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  loadData()
}

const handleStatusChange = () => {
  pageNum.value = 1
  loadData()
}

const openAudit = (row) => {
  currentProject.value = row
  auditForm.value = { status: 1, reason: '' }
  dialogVisible.value = true
}

const submitAudit = async () => {
  if (auditForm.value.status === 2 && !auditForm.value.reason) {
    return ElMessage.warning('驳回时必须填写原因')
  }

  submitLoading.value = true
  try {
    // 对应后端处理接口
    const res = await request.post('/admin/project/audit', {
      id: currentProject.value.id,
      auditStatus: auditForm.value.status,
      rejectReason: auditForm.value.reason
    })
    if (res.code === "200") {
      ElMessage.success('审核处理成功')
      dialogVisible.value = false
      loadData()
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
.audit-container { padding: 20px; background: #f0f2f5; min-height: calc(100vh - 84px); }
.money { color: #f56c6c; font-weight: bold; }
.pagination-container { margin-top: 25px; display: flex; justify-content: flex-end; }
.audit-result-section { margin-top: 25px; padding-top: 20px; border-top: 1px solid #eee; }
.form-label { font-weight: bold; margin-bottom: 12px; color: #606266; font-size: 14px; }
.reason-input { margin-top: 20px; }
</style>