<template>
  <div class="projects-page">
    <div class="projects-header">
      <div class="header-content">
        <h1>浏览爱心项目</h1>
        <p>每一份善意，都在为世界点亮微光</p>

        <div class="search-bar-wrapper">
          <el-input
              v-model="queryParams.title"
              placeholder="搜索项目标题、求助人或关键词..."
              size="large"
              clearable
              @keyup.enter="handleSearch"
              class="search-input"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon> 搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <div class="projects-container">
      <div class="filter-section">
        <div class="filter-group">
          <span class="filter-label">项目分类：</span>
          <el-radio-group v-model="queryParams.type" @change="handleSearch" size="default">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button :label="1">🏥 大病求助</el-radio-button>
            <el-radio-button :label="2">📚 助学圆梦</el-radio-button>
            <el-radio-button :label="3">🌪️ 灾害救援</el-radio-button>
          </el-radio-group>
        </div>

        <div class="filter-group">
          <span class="filter-label">当前状态：</span>
          <el-radio-group v-model="queryParams.status" @change="handleSearch" size="default">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button :label="1">募集中</el-radio-button>
            <el-radio-button :label="2">已圆满</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div v-loading="loading" class="list-wrapper">
        <el-row :gutter="25" v-if="projectList.length > 0">
          <el-col :span="8" v-for="p in projectList" :key="p.id">
            <el-card class="premium-project-card" :body-style="{ padding: '0px' }" @click="goDetail(p.id)">
              <div class="img-container">
                <el-tag :type="getTypeTag(p.projectType)" class="floating-tag" effect="dark">
                  {{ getTypeText(p.projectType) }}
                </el-tag>
                <img :src="p.cover || getCover(p.projectType)" class="project-img" />
                <div class="img-overlay" v-if="p.status === 2">
                  <div class="success-seal">已完成</div>
                </div>
              </div>

              <div class="card-body">
                <h3 class="p-title">{{ p.title }}</h3>
                <p class="p-intro">{{ p.summary }}</p>

                <div class="progress-info">
                  <div class="info-row">
                    <span class="raised">已筹 <b>¥{{ p.raisedAmount }}</b></span>
                    <span class="percent">{{ calculatePercent(p.raisedAmount, p.targetAmount) }}%</span>
                  </div>
                  <el-progress
                      :percentage="calculatePercent(p.raisedAmount, p.targetAmount)"
                      :show-text="false"
                      :stroke-width="10"
                      color="#f56c6c"
                  />
                  <div class="info-row target-row">
                    <span>目标：¥{{ p.targetAmount }}</span>
                    <span>120+ 人捐助</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-else description="暂无匹配的公益项目，换个关键词试试？" />

        <div class="pagination-area">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :page-size="queryParams.size"
              v-model:current-page="queryParams.page"
              @current-change="fetchProjects"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import request from '../utils/request.js'

const router = useRouter()
const loading = ref(false)
const projectList = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 9,
  title: '',
  type: '',
  status: ''
})

const fetchProjects = async () => {
  loading.value = true
  try {
    const res = await request.get('/project/page', { params: queryParams })
    if (res.code === "200") {
      projectList.value = res.data.list
      total.value = res.data.total
    }
  } catch (err) {
    console.error('获取列表失败', err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  fetchProjects()
}

const goDetail = (id) => router.push(`/project/${id}`)

const calculatePercent = (raised, target) => {
  if (!target) return 0
  return Math.min(Math.floor((raised / target) * 100), 100)
}

const getTypeText = (t) => ({ 1: '大病求助', 2: '助学圆梦', 3: '灾害救援' }[t] || '其他')
const getTypeTag = (t) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[t] || 'info')
const getCover = (t) => {
  const imgs = {
    1: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=400',
    2: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400',
    3: 'https://images.unsplash.com/photo-1461532257246-777de18cd58b?w=400'
  }
  return imgs[t]
}

onMounted(fetchProjects)
</script>

<style scoped>
.projects-page { background-color: #f8fafc; min-height: 100vh; }

.projects-header {
  height: 300px;
  background: linear-gradient(135deg, #f56c6c 0%, #ff8e8e 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: white;
}

.header-content h1 { font-size: 32px; margin-bottom: 10px; }
.header-content p { font-size: 16px; opacity: 0.9; margin-bottom: 30px; }

.search-bar-wrapper { width: 600px; margin: 0 auto; }
.search-input :deep(.el-input__wrapper) { border-radius: 30px 0 0 30px; padding-left: 20px; }
.search-input :deep(.el-input-group__append) { border-radius: 0 30px 30px 0; background-color: #333; color: #fff; border: none; }

.projects-container { padding: 40px 10%; margin-top: -40px; }

.filter-section {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.filter-group { display: flex; align-items: center; margin-bottom: 15px; }
.filter-group:last-child { margin-bottom: 0; }
.filter-label { width: 80px; font-weight: bold; color: #64748b; font-size: 14px; }

/* 卡片美化 */
.premium-project-card { border-radius: 15px; border: none; transition: all 0.3s; cursor: pointer; height: 100%; }
.premium-project-card:hover { transform: translateY(-8px); box-shadow: 0 15px 30px rgba(0,0,0,0.1); }

.img-container { position: relative; height: 180px; overflow: hidden; }
.project-img { width: 100%; height: 100%; object-fit: cover; }
.floating-tag { position: absolute; top: 12px; left: 12px; }

.img-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(255,255,255,0.6); display: flex; justify-content: center; align-items: center; }
.success-seal { border: 4px solid #67c23a; color: #67c23a; padding: 5px 15px; font-size: 24px; font-weight: 800; border-radius: 8px; transform: rotate(-15deg); text-transform: uppercase; }

.card-body { padding: 20px; }
.p-title { font-size: 18px; color: #1e293b; margin-bottom: 12px; line-height: 1.4; height: 50px; overflow: hidden; }
.p-intro { font-size: 13px; color: #64748b; line-height: 1.6; height: 40px; margin-bottom: 20px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.info-row { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 8px; }
.raised b { font-size: 20px; color: #f56c6c; }
.percent { font-weight: bold; color: #f56c6c; }
.target-row { margin-top: 10px; font-size: 12px; color: #94a3b8; }

.pagination-area { margin-top: 50px; display: flex; justify-content: center; }
</style>