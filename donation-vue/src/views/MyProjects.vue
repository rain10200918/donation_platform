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
          <div class="stat-box">
            <div class="stat-icon-wrapper blue-bg">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">发起项目</div>
              <div class="stat-value">{{ myProjects.length }} <small>个</small></div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-box">
            <div class="stat-icon-wrapper red-bg">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">累计获助</div>
              <div class="stat-value">{{ totalRaised.toLocaleString() }} <small>元</small></div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="stat-box-notice">
            <div class="notice-icon"><el-icon><Bell /></el-icon></div>
            <div class="notice-content">
              <strong>管理建议：</strong>
              <span>审核通过后的项目，建议每隔 10 天更新一次动态，这能显著提高 40% 的筹款成功率。</span>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-card class="list-card" shadow="never">
        <template #header>
          <div class="list-header">
            <div class="list-title-box">
              <span class="title">项目管理清单</span>
              <span class="count-tag">{{ myProjects.length }}</span>
            </div>
            <el-button type="danger" size="default" round @click="router.push('/publish')" class="glow-btn">
              + 发起新求助
            </el-button>
          </div>
        </template>

        <el-table :data="myProjects" stripe v-loading="loading" class="custom-table">
          <el-table-column label="求助项目" min-width="280">
            <template #default="scope">
              <div class="project-info-cell">
                <div class="image-container">
                  <el-image :src="getCover(scope.row.projectType)" class="project-thumb" />
                  <div class="type-mini-tag">{{ getTypeText(scope.row.projectType) }}</div>
                </div>
                <div class="project-detail">
                  <div class="p-title">{{ scope.row.title }}</div>
                  <div class="p-time">
                    <el-icon><Calendar /></el-icon> {{ formatDate(scope.row.createTime) }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="筹款进度" width="220">
            <template #default="scope">
              <div class="prog-wrapper">
                <div class="prog-text">
                  <span class="raised-text">已筹 <b>¥{{ scope.row.raisedAmount }}</b></span>
                  <span class="percent-text">{{ calculatePercent(scope.row.raisedAmount, scope.row.targetAmount) }}%</span>
                </div>
                <el-progress
                    :percentage="calculatePercent(scope.row.raisedAmount, scope.row.targetAmount)"
                    :color="scope.row.status === 2 ? '#67c23a' : '#f56c6c'"
                    :stroke-width="10"
                    :show-text="false"
                />
                <div class="target-text">目标: ¥{{ scope.row.targetAmount }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="审核状态" width="130" align="center">
            <template #default="scope">
              <el-tooltip
                  v-if="scope.row.auditStatus === 2"
                  :content="'驳回原因: ' + (scope.row.rejectReason || '未提供具体原因')"
                  placement="top"
              >
                <el-tag type="danger" effect="dark" round class="status-tag is-clickable">
                  已驳回
                </el-tag>
              </el-tooltip>

              <el-tag v-else :type="getAuditTag(scope.row.auditStatus)" effect="light" round class="status-tag">
                {{ getAuditText(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="管理操作" width="180" align="right" fixed="right">
            <template #default="scope">
              <template v-if="scope.row.auditStatus === 1">
                <el-button size="small" type="primary" link @click="router.push(`/project/${scope.row.id}`)">详情</el-button>
                <el-button size="small" type="success" link @click="openProgressDialog(scope.row)">发布进展</el-button>
              </template>

              <template v-else-if="scope.row.auditStatus === 0">
                <span class="wait-text">排队审核中...</span>
              </template>

              <template v-else-if="scope.row.auditStatus === 2">
                <el-button size="small" type="danger" link @click="handleEdit(scope.row)">重新修改</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>

        <div class="empty-box" v-if="myProjects.length === 0">
          <el-empty :image-size="200" description="您还没有发起过求助项目">
            <el-button type="primary" round @click="router.push('/publish')">立即发起第一笔求助</el-button>
          </el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { InfoFilled, DataLine, Wallet, Bell, Calendar } from '@element-plus/icons-vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const myProjects = ref([])

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
const calculatePercent = (raised, target) => target ? Math.min(Math.floor((raised / target) * 100), 100) : 0

const getAuditText = (s) => ({ 0: '待审核', 1: '审核通过', 2: '已驳回' }[s] || '未知')
const getAuditTag = (s) => ({ 0: 'info', 1: 'success', 2: 'danger' }[s] || 'info')
const getTypeText = (t) => ({ 1: '个人', 2: '公益', 3: '紧急' }[t] || '其他')

const formatDate = (s) => s ? s.replace('T', ' ').split(':')[0] : '-'

const getCover = (type) => {
  const imgs = {
    1: 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=160',
    2: 'https://images.unsplash.com/photo-1581579438747-104c53d7fbc4?w=160',
    3: 'https://images.unsplash.com/photo-1509099836639-18ba1795216d?w=160'
  }
  return imgs[type] || 'https://via.placeholder.com/160x100'
}

const openProgressDialog = (p) => ElMessage.info(`请在详情页点击“发布进展”公告`)
const handleEdit = (p) => router.push(`/publish?id=${p.id}`)

onMounted(fetchMyProjects)
</script>

<style scoped>
.my-projects-page { background-color: #f8fafc; min-height: 100vh; }

/* 头部 Banner */
.page-header-banner {
  background: linear-gradient(135deg, #f56c6c 0%, #ff8e8e 100%);
  padding: 40px 10% 90px;
  color: white;
}
.header-title { font-size: 26px; font-weight: 800; color: white; }
.header-subtitle { margin-top: 12px; opacity: 0.85; font-size: 15px; }
:deep(.el-page-header__title), :deep(.el-page-header__content) { color: white !important; }

/* 主体布局 */
.content-body { margin-top: -60px; padding: 0 10% 50px; }

/* 统计卡片美化 */
.stat-box {
  background: white; padding: 20px; border-radius: 16px;
  display: flex; align-items: center; gap: 15px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.05); height: 100px;
}
.stat-icon-wrapper {
  width: 50px; height: 50px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 24px;
}
.blue-bg { background: #eff6ff; color: #3b82f6; }
.red-bg { background: #fef2f2; color: #ef4444; }

.stat-label { font-size: 13px; color: #64748b; margin-bottom: 4px; }
.stat-value { font-size: 24px; font-weight: 800; color: #1e293b; }
.stat-value small { font-size: 14px; color: #94a3b8; font-weight: 400; }

.stat-box-notice {
  background: #fff7ed; border: 1px solid #ffedd5; border-radius: 16px;
  padding: 20px; height: 100px; display: flex; align-items: center; gap: 15px;
}
.notice-icon { font-size: 24px; color: #f97316; }
.notice-content { font-size: 14px; color: #9a3412; line-height: 1.5; }

/* 列表美化 */
.list-card { border-radius: 16px; border: none; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }
.list-header { display: flex; justify-content: space-between; align-items: center; }
.list-title-box { display: flex; align-items: center; gap: 10px; }
.list-header .title { font-size: 18px; font-weight: 800; color: #1e293b; }
.count-tag { background: #f1f5f9; color: #64748b; padding: 2px 10px; border-radius: 10px; font-size: 12px; }

/* 表格样式优化 */
.project-info-cell { display: flex; align-items: center; gap: 15px; }
.image-container { position: relative; width: 100px; height: 65px; overflow: hidden; border-radius: 8px; }
.project-thumb { width: 100%; height: 100%; object-fit: cover; }
.type-mini-tag {
  position: absolute; bottom: 0; right: 0; background: rgba(0,0,0,0.6);
  color: white; font-size: 10px; padding: 2px 6px; border-top-left-radius: 6px;
}
.p-title { font-weight: 700; color: #334155; font-size: 15px; margin-bottom: 6px; }
.p-time { font-size: 12px; color: #94a3b8; display: flex; align-items: center; gap: 4px; }

/* 进度条 */
.prog-wrapper { width: 100%; }
.prog-text { display: flex; justify-content: space-between; margin-bottom: 8px; }
.raised-text { font-size: 12px; color: #64748b; }
.raised-text b { color: #1e293b; font-size: 14px; }
.percent-text { color: #f56c6c; font-weight: 800; font-size: 14px; }
.target-text { font-size: 11px; color: #94a3b8; margin-top: 6px; }

.status-tag { padding: 0 12px; font-weight: 600; }
.is-clickable { cursor: help; }
.wait-text { color: #94a3b8; font-size: 13px; font-style: italic; }

.glow-btn { box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4); }
</style>