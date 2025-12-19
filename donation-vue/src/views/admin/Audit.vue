<template>
  <div class="audit-container">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">{{ item.title }}</div>
            <div class="stat-value" :class="item.color">{{ item.value }}</div>
          </div>
          <el-icon class="stat-icon"><component :is="item.icon" /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="main-table-card">
      <template #header>
        <div class="card-header-wrapper">
          <div class="left-tabs">
            <el-tabs v-model="auditStatusFilter" @tab-change="handleStatusChange" class="custom-tabs">
              <el-tab-pane label="待审批" name="0" />
              <el-tab-pane label="审批通过" name="1" />
              <el-tab-pane label="已驳回" name="2" />
            </el-tabs>
          </div>
          <div class="right-ops">
            <el-input
                v-model="searchQuery"
                placeholder="搜索项目标题/关键词"
                prefix-icon="Search"
                class="search-input"
                clearable
                @keyup.enter="loadData"
            />
            <el-button icon="Refresh" @click="loadData">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="auditList" v-loading="loading" class="modern-table" row-class-name="table-row">
        <el-table-column label="项目基本信息" min-width="300">
          <template #default="{row}">
            <div class="project-info-cell">
              <div class="project-name">{{ row.title }}</div>
              <div class="project-sub">
                <el-tag size="small" effect="plain" type="info" class="id-tag">ID: {{ row.id }}</el-tag>
                <span class="time-text">提交于: {{ formatDate(row.createTime) }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="筹款目标" width="160">
          <template #default="{row}">
            <div class="money-cell">
              <span class="currency">¥</span>
              <span class="val">{{ row.targetAmount?.toLocaleString() }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="审核状态" width="140">
          <template #default="{row}">
            <div class="status-wrapper">
              <span class="status-dot" :class="'dot-' + row.auditStatus"></span>
              <span class="status-text">{{ auditStatusMap[row.auditStatus]?.text }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{row}">
            <el-button
                v-if="row.auditStatus === 0"
                type="primary"
                link
                class="action-btn"
                @click="openAudit(row)"
            >
              <el-icon><EditPen /></el-icon>去审核
            </el-button>
            <el-button
                v-else
                type="default"
                link
                class="action-btn"
                @click="openAudit(row)"
            >
              <el-icon><Document /></el-icon>详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-pane">
        <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="currentProject?.auditStatus === 0 ? '项目立项审核' : '项目档案详情'"
        width="680px"
        class="modern-dialog"
        destroy-on-close
    >
      <div v-if="currentProject" class="audit-modal-body">
        <div class="info-section">
          <h4 class="section-title">核心信息</h4>
          <el-descriptions :column="2" border class="custom-desc">
            <el-descriptions-item label="项目标题" :span="2">{{ currentProject.title }}</el-descriptions-item>
            <el-descriptions-item label="目标金额">
              <span class="highlight-money">¥ {{ currentProject.targetAmount?.toLocaleString() }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDate(currentProject.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="info-section">
          <h4 class="section-title">申请诉求/背景</h4>
          <div class="summary-content">
            {{ currentProject.summary }}
          </div>
        </div>

        <div class="info-section audit-action-area">
          <h4 class="section-title">审核结论</h4>

          <div v-if="currentProject.auditStatus === 0" class="audit-form-box">
            <el-radio-group v-model="auditForm.status" class="modern-radio-group">
              <el-radio-button :label="1">审核通过</el-radio-button>
              <el-radio-button :label="2">驳回修改</el-radio-button>
            </el-radio-group>

            <transition name="el-zoom-in-top">
              <div v-if="auditForm.status === 2" class="reason-input-wrapper">
                <p class="input-label">驳回原因 (必填):</p>
                <el-input
                    v-model="auditForm.reason"
                    type="textarea"
                    :rows="4"
                    placeholder="请详细描述驳回原因，指导用户进行修改..."
                />
              </div>
            </transition>
          </div>

          <div v-else class="audit-result-display">
            <el-alert
                :title="currentProject.auditStatus === 1 ? '审批通过' : '审批驳回'"
                :type="currentProject.auditStatus === 1 ? 'success' : 'error'"
                :description="currentProject.auditStatus === 2 ? ('驳回理由：' + currentProject.rejectReason) : '该项目已符合准入标准，准予在平台公开筹款。'"
                show-icon
                :closable="false"
            />
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消返回</el-button>
          <el-button
              v-if="currentProject?.auditStatus === 0"
              type="primary"
              :loading="submitLoading"
              @click="submitAudit"
          >
            确认提交
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Clock, Finished, Warning, Money, EditPen, Document } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const auditStatusFilter = ref("0") // Element Plus Tabs 默认是字符串
const auditList = ref([])
const currentProject = ref(null)
const searchQuery = ref('')

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 顶部统计看板数据
const statCards = ref([
  { title: '待处理任务', value: '-', icon: 'Clock', color: 'blue', key: 'pendingCount' },
  { title: '累计已批项目', value: '-', icon: 'Finished', color: 'green', key: 'approvedCount' },
  { title: '高风险拦截', value: '-', icon: 'Warning', color: 'red', key: 'rejectedCount' },
  { title: '救助资金规模', value: '-', icon: 'Money', color: '', key: 'totalAmount' }
])

const auditForm = ref({
  status: 1,
  reason: ''
})

const auditStatusMap = {
  0: { text: '待审批', type: 'primary' },
  1: { text: '审批通过', type: 'success' },
  2: { text: '已驳回', type: 'danger' }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/project/list', {
      params: {
        status: auditStatusFilter.value,
        title: searchQuery.value, // 增加标题搜索能力
        pageNum: pageNum.value,
        pageSize: pageSize.value
      }
    })
    if (res.code === "200") {
      auditList.value = res.data.list || []
      total.value = res.data.total || 0
      // 可以在这里根据后端返回的统计字段动态更新 statCards
    }
  } catch (err) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}
const loadStats = async () => {
  try {
    const res = await request.get('/admin/project/stats')
    if (res.code === "200") {
      // 动态更新看板数据
      const data = res.data
      statCards.value.forEach(card => {
        if (card.key === 'pendingCount') card.value = data.pendingCount
        if (card.key === 'approvedCount') card.value = data.approvedCount
        if (card.key === 'rejectedCount') card.value = data.rejectedCount
        if (card.key === 'totalAmount') {
          // 格式化金额，比如 128500 变成 12.8w
          card.value = '¥' + (data.totalAmount >= 10000
              ? (data.totalAmount / 10000).toFixed(1) + 'w'
              : data.totalAmount)
        }
      })
    }
  } catch (err) {
    console.error('统计数据加载失败', err)
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
    return ElMessage.warning('请填写驳回具体原因')
  }

  submitLoading.value = true
  try {
    const res = await request.post('/admin/project/audit', {
      id: currentProject.value.id,
      auditStatus: auditForm.value.status,
      rejectReason: auditForm.value.reason
    })
    if (res.code === "200") {
      ElMessage.success('审批结论已生效')
      dialogVisible.value = false
      loadData()
    }
  } catch (err) {
    ElMessage.error('操作异常')
  } finally {
    submitLoading.value = false
  }
}

const formatDate = (s) => s ? s.replace('T', ' ').substring(0, 16) : '-'

onMounted(() => {
  loadData()  // 加载列表
  loadStats() // 加载看板
})
</script>

<style scoped>
.audit-container {
  padding: 24px;
  background-color: #f6f8fb;
  min-height: 100vh;
}

/* 看板样式 */
.stat-row { margin-bottom: 24px; }
.stat-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}
.stat-content { display: flex; flex-direction: column; }
.stat-label { font-size: 14px; color: #64748b; margin-bottom: 8px; }
.stat-value { font-size: 26px; font-weight: 700; color: #1e293b; }
.stat-value.blue { color: #3b82f6; }
.stat-value.green { color: #10b981; }
.stat-value.red { color: #ef4444; }
.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 44px;
  color: rgba(148, 163, 184, 0.15);
}

/* 表格卡片 */
.main-table-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.card-header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-tabs :deep(.el-tabs__header) {
  margin: 0;
  border-bottom: none;
}
.custom-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  height: 48px;
}

.right-ops { display: flex; gap: 12px; }
.search-input { width: 280px; }

/* 表格内部 */
.project-info-cell { display: flex; flex-direction: column; gap: 6px; }
.project-name {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
  cursor: pointer;
}
.project-name:hover { color: #3b82f6; }
.project-sub { display: flex; align-items: center; gap: 10px; }
.time-text { font-size: 12px; color: #94a3b8; }

.money-cell { font-family: 'Din', 'PingFang SC', sans-serif; }
.money-cell .currency { font-size: 12px; color: #ef4444; margin-right: 2px; }
.money-cell .val { font-size: 18px; font-weight: 700; color: #ef4444; }

/* 状态引导 */
.status-wrapper { display: flex; align-items: center; gap: 8px; }
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
}
.dot-0 { background: #3b82f6; box-shadow: 0 0 6px #3b82f6; animation: breathe 2s infinite; }
.dot-1 { background: #10b981; }
.dot-2 { background: #ef4444; }

@keyframes breathe {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

.action-btn { font-size: 14px; font-weight: 500; display: flex; align-items: center; gap: 4px; }

/* 对话框 */
.section-title {
  font-size: 14px;
  font-weight: 700;
  color: #475569;
  margin: 20px 0 12px;
  padding-left: 10px;
  border-left: 4px solid #3b82f6;
}
.summary-content {
  background: #f8fafc;
  padding: 16px;
  border-radius: 8px;
  color: #334155;
  font-size: 14px;
  line-height: 1.7;
}
.highlight-money { color: #ef4444; font-weight: 800; font-size: 18px; }

.audit-action-area {
  margin-top: 30px;
  padding-top: 10px;
}
.modern-radio-group :deep(.el-radio-button__inner) {
  width: 140px;
  height: 44px;
  line-height: 22px;
}
.reason-input-wrapper { margin-top: 20px; }
.input-label { font-size: 13px; color: #64748b; margin-bottom: 8px; }

.pagination-pane {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* 全局覆盖 */
:deep(.el-table__header th) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 700;
}
</style>