<template>
  <div class="my-projects-page">
    <div class="page-header-banner">
      <div class="header-content">
        <el-page-header @back="router.push('/home')" title="返回首页">
          <template #content>
            <span class="header-title"> ❤️ 我的求助管理 </span>
          </template>
        </el-page-header>
        <p class="header-subtitle">在这里管理您发起的每一个爱心项目，及时公开进展让信任传递</p>
      </div>
    </div>

    <div class="content-body">
      <el-row :gutter="20" class="stat-row">
        <el-col :span="6">
          <div class="stat-box blue">
            <div class="stat-icon">📊</div>
            <div class="stat-info">
              <div class="stat-label">发起项目</div>
              <div class="stat-value">{{ myProjects.length }} <small>个</small></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box red">
            <div class="stat-icon">💰</div>
            <div class="stat-info">
              <div class="stat-label">累计获助</div>
              <div class="stat-value">{{ totalRaised.toLocaleString() }} <small>元</small></div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stat-box notice">
            <el-icon><InfoFilled /></el-icon>
            <span class="notice-text">温馨提示：审核通过后的项目请务必每 7-15 天发布一次进展。</span>
          </div>
        </el-col>
      </el-row>

      <el-card class="list-card" shadow="hover">
        <template #header>
          <div class="list-header">
            <span class="title">项目管理清单</span>
            <el-button type="danger" size="default" round @click="router.push('/publish')">发起新求助</el-button>
          </div>
        </template>

        <el-table :data="myProjects" stripe v-loading="loading" header-cell-class-name="table-header-cell">
          <el-table-column label="项目信息" min-width="250">
            <template #default="scope">
              <div class="project-info-cell">
                <el-image :src="getCover(scope.row.projectType)" class="project-thumb" />
                <div class="project-detail">
                  <div class="p-title">{{ scope.row.title }}</div>
                  <div class="p-time">发布时间: {{ formatDate(scope.row.createTime) }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="筹款进度" width="220">
            <template #default="scope">
              <div class="prog-wrapper">
                <div class="prog-text">
                  <span>已筹 ¥{{ scope.row.raisedAmount }}</span>
                  <span class="percent">{{ calculatePercent(scope.row.raisedAmount, scope.row.targetAmount) }}%</span>
                </div>
                <el-progress
                    :percentage="calculatePercent(scope.row.raisedAmount, scope.row.targetAmount)"
                    :color="customColors"
                    :stroke-width="12"
                    class="custom-progress"
                />
              </div>
            </template>
          </el-table-column>

          <el-table-column label="审核状态" width="130" align="center">
            <template #default="scope">
              <el-tag :type="getStatusTag(scope.row.auditStatus)" effect="light" class="status-tag">
                {{ getStatusText(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="管理操作" width="200" align="right" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" link @click="router.push(`/project/${scope.row.id}`)">详情</el-button>
              <el-divider direction="vertical" />
              <el-button
                  v-if="scope.row.auditStatus === 1"
                  size="small"
                  type="success"
                  link
                  @click="openProgressDialog(scope.row)"
              >发布进展</el-button>
              <el-button v-if="scope.row.auditStatus === 2" size="small" type="danger" link>查看原因</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="myProjects.length === 0" :image-size="200" description="您还没有发起过求助，爱心从这里开始..." />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { InfoFilled } from '@element-plus/icons-vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const myProjects = ref([])

// 进度条颜色映射
const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 },
]

const fetchMyProjects = async () => {
  loading.value = true
  try {
    const res = await request.get('/project/my-projects')
    if (res.code === "200") myProjects.value = res.data
  } catch (err) {
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

const totalRaised = computed(() => myProjects.value.reduce((sum, p) => sum + (p.raisedAmount || 0), 0))

const calculatePercent = (raised, target) => {
  if (!target) return 0
  return Math.min(Math.floor((raised / target) * 100), 100)
}

const getStatusText = (s) => ({ 0: '待审核', 1: '审核通过', 2: '已驳回' }[s] || '审核中')
const getStatusTag = (s) => ({ 0: 'info', 1: 'success', 2: 'danger' }[s] || 'info')
const formatDate = (s) => s ? s.split('T')[0] : '-'

const getCover = (type) => {
  const imgs = {
    1: 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=100',
    2: 'https://images.unsplash.com/photo-1581579438747-104c53d7fbc4?w=100'
  }
  return imgs[type] || 'https://via.placeholder.com/100x60'
}

const openProgressDialog = (project) => ElMessage.info(`发布进展功能开发中...`)

onMounted(fetchMyProjects)
</script>

<style scoped>
.my-projects-page { background-color: #fcfcfc; min-height: 100vh; }

/* 头部 Banner 样式 */
.page-header-banner {
  background: linear-gradient(135deg, #ff7e5f 0%, #feb47b 100%);
  padding: 30px 10% 80px;
  color: white;
}
.header-title { font-size: 24px; font-weight: bold; color: white; }
.header-subtitle { margin-top: 10px; opacity: 0.9; font-size: 14px; }
:deep(.el-page-header__title), :deep(.el-page-header__content) { color: white !important; }

/* 内容主体上移 */
.content-body { margin-top: -50px; padding: 0 10% 50px; }

/* 统计盒子样式 */
.stat-box {
  background: white;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  height: 90px;
}
.stat-icon { font-size: 30px; margin-right: 15px; }
.stat-label { font-size: 13px; color: #94a3b8; }
.stat-value { font-size: 24px; font-weight: bold; color: #1e293b; }
.stat-value small { font-size: 14px; font-weight: normal; }

.stat-box.notice { background: rgba(255, 255, 255, 0.8); border: 1px dashed #feb47b; justify-content: center; }
.notice-text { margin-left: 10px; color: #c2410c; font-size: 14px; }

/* 列表卡片 */
.list-card { border-radius: 12px; border: none; }
.list-header { display: flex; justify-content: space-between; align-items: center; }
.list-header .title { font-size: 18px; font-weight: bold; }

/* 表格内项目信息 */
.project-info-cell { display: flex; align-items: center; gap: 15px; }
.project-thumb { width: 80px; height: 50px; border-radius: 4px; object-fit: cover; }
.p-title { font-weight: bold; color: #334155; margin-bottom: 5px; }
.p-time { font-size: 12px; color: #94a3b8; }

/* 进度条美化 */
.prog-wrapper { width: 100%; }
.prog-text { display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 6px; color: #64748b; }
.percent { font-weight: bold; color: #f56c6c; }
.custom-progress :deep(.el-progress-bar__outer) { background-color: #f1f5f9; }

:deep(.table-header-cell) { background-color: #f8fafc !important; color: #475569; font-weight: bold; }
.status-tag { border-radius: 20px; padding: 0 15px; }
</style>