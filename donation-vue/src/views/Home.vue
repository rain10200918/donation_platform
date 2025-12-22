<template>
  <div class="common-layout">
    <el-container>
      <el-main class="main-content">
        <el-carousel height="520px" motion-blur class="banner">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-item" :style="{ backgroundImage: `url(${item.img})` }">
              <div class="banner-overlay"></div>
              <div class="banner-content">
                <el-tag class="banner-tag" effect="dark" type="danger">紧急募集中</el-tag>
                <h2 class="banner-title">{{ item.title }}</h2>
                <p class="banner-desc">{{ item.desc }}</p>
                <div class="banner-actions">
                  <el-button type="danger" size="large" class="glow-btn">立即提供帮助</el-button>
                  <el-button size="large" plain class="white-btn" @click="router.push(item.link)">了解项目故事</el-button>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>

        <div class="statistics-wrapper">
          <div class="statistics-card">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-box">
                  <el-icon class="stat-icon"><Money /></el-icon>
                  <el-statistic title="累计筹集善款" :value="totalRaised" />
                  <span class="unit">元</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-box">
                  <el-icon class="stat-icon"><User /></el-icon>
                  <el-statistic title="爱心捐助人次" :value="totalDonations" />
                  <span class="unit">次</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-box">
                  <el-icon class="stat-icon"><CircleCheck /></el-icon>
                  <el-statistic title="已圆满资助项目" :value="completedProjects" />
                  <span class="unit">个</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>

        <div class="project-container">
          <div class="section-header">
            <div class="title-area">
              <h3 class="main-title">正在众筹</h3>
              <p class="sub-title">您的每一次转发和捐赠，都是求助者重生的希望</p>
            </div>
            <el-button type="primary" link @click="router.push('/projects')">
              查看全部项目 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>

          <el-row :gutter="25">
            <el-col :span="8" v-for="p in projects" :key="p.id">

              <el-skeleton :loading="loading" animated>

                <template #template>
                  <el-card class="modern-card" :body-style="{ padding: '0px' }">
                    <el-skeleton-item variant="image" style="height: 230px; width: 100%" />
                    <div style="padding: 25px">
                      <el-skeleton-item variant="h3" style="width: 60%; margin-bottom: 15px" />
                      <el-skeleton-item variant="p" style="width: 100%" />
                      <el-skeleton-item variant="p" style="width: 80%; margin-top: 10px" />
                      <div style="margin-top: 20px; display: flex; justify-content: space-between">
                        <el-skeleton-item variant="text" style="width: 30%" />
                        <el-skeleton-item variant="button" style="width: 20%" />
                      </div>
                    </div>
                  </el-card>
                </template>

                <template #default>
                  <el-card class="modern-card" :body-style="{ padding: '0px' }" @click="goDetail(p.id)">
                    <div class="img-wrapper">
                      <el-tag class="project-type-tag" :type="getTypeTag(p.projectType)" effect="dark">
                        {{ getTypeText(p.projectType) }}
                      </el-tag>
                      <img :src="displayCover(p)" class="project-image" />
                      <div class="image-mask"><span>查看详情</span></div>
                    </div>

                    <div class="card-info">
                      <h4 class="project-title">{{ p.title }}</h4>
                      <p class="project-summary">{{ p.summary }}</p>

                      <div class="progress-section">
                        <div class="progress-text">
                          <span class="percent-num">{{ calculatePercent(p.raisedAmount, p.targetAmount) }}%</span>
                          <span class="raised-num">已筹 ¥{{ p.raisedAmount }}</span>
                        </div>
                        <el-progress
                            :percentage="calculatePercent(p.raisedAmount, p.targetAmount)"
                            :show-text="false"
                            :stroke-width="10"
                            :color="p.status === 2 ? '#67c23a' : '#f56c6c'"
                        />
                      </div>

                      <div class="card-footer">
                        <div class="target-box">
                          <span class="label">目标金额</span>
                          <span class="value">¥{{ p.targetAmount }}</span>
                        </div>
                        <el-button type="primary" round class="support-btn">帮助他</el-button>
                      </div>
                    </div>
                  </el-card>
                </template>
              </el-skeleton>

            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Money, User, CircleCheck, ArrowRight
} from '@element-plus/icons-vue'
import request from '../utils/request.js'

const router = useRouter()

// 状态数据
const projects = ref([])
const totalRaised = ref(1258000)
const totalDonations = ref(45600)
const completedProjects = ref(128)
const userInfo = ref({ nickName: '未登录', avatar: '' })
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const banners = ref([
  {
    id: 1,
    title: '山村小学图书室计划',
    desc: '让每一个孩子都能在书海翱翔',
    img: 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=2000',
    link: '/story/2' // 对应大山书声故事
  },
  {
    id: 2,
    title: '先心病女孩的微笑',
    desc: '众志成城，让“小苹果”重获新生',
    img: 'https://images.unsplash.com/photo-1516627145497-ae6968895b74?q=80&w=2000',
    link: '/story/1' // 对应小苹果故事
  }
])
const loading = ref(true)
// 初始化加载数据
const loadHomeData = async () => {
  loading.value = true
  try {
    const projectRes = await request.get('/project/page', { params: { page: 1, size: 6 } })
    if (projectRes.code === "200") projects.value = projectRes.data.list

    const userRes = await request.get('/user/info')
    if (userRes.code === "200") userInfo.value = userRes.data
  } catch (err) {
    console.error('加载首页数据失败', err)
  } finally {
    setTimeout(() => {
      loading.value = false // 停止加载，显示真实卡片
    }, 500)
  }
}

const calculatePercent = (raised, target) => {
  if (!target || target === 0) return 0
  const p = Math.floor((raised / target) * 100)
  return p > 100 ? 100 : p
}

const getTypeText = (t) => ({ 1: '个人求助', 2: '公益项目', 3: '紧急救助' }[t] || '其他')
const getTypeTag = (t) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[t] || 'info')
const getCover = (t) => {
  const defaultImgs = {
    1: 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=600', // 医疗相关
    2: 'https://images.unsplash.com/photo-1581579438747-104c53d7fbc4?w=600', // 公益助学
    3: 'https://images.unsplash.com/photo-1509099836639-18ba1795216d?w=600'  // 紧急救助
  }
  return defaultImgs[t] || 'https://via.placeholder.com/600x400?text=爱心求助'
}

const goDetail = (id) => router.push(`/project/${id}`)
const displayCover = (row) => {
  // 如果后端返回了图片字段，且不为空
  if (row.picture) {
    // 即使存的是多图(url1,url2)，我们也只取第一张作为首页封面
    return row.picture.split(',')[0]
  }
  // 否则走分类占位图
  return getCover(row.projectType)
}
const handlePublish = async () => {
  const token = sessionStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录后再发起求助')
    router.push('/login'); return
  }
  try {
    const res = await request.get('/user-auth/detail')
    if (res.code === "200") {
      const auth = res.data
      if (!auth) showAuthConfirm('您尚未进行实名认证，请先完成认证以确保项目真实性。')
      else if (auth.authStatus === 1) router.push('/publish')
      else if (auth.authStatus === 0) ElMessage.warning('您的实名认证正在审核中，请稍后再试')
      else if (auth.authStatus === 2) showAuthConfirm(`您的实名认证已被驳回（原因：${auth.rejectReason}），请修改后重新提交。`)
    }
  } catch (err) {
    ElMessage.error('权限检查失败')
  }
}

const showAuthConfirm = (message) => {
  ElMessageBox.confirm(message, '身份校验', {
    confirmButtonText: '去认证',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => router.push('/user-auth')).catch(() => {})
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
    sessionStorage.clear(); ElMessage.success('已安全退出'); router.replace('/login')
  })
}

onMounted(loadHomeData)
</script>

<style scoped>
.header {
  display: flex; align-items: center; background: rgba(255,255,255,0.98);
  height: 70px; padding: 0 60px; position: sticky; top: 0; z-index: 1000;
  box-shadow: 0 2px 15px rgba(0,0,0,0.05);
}
.logo { display: flex; align-items: center; margin-right: 40px; }
.heart-icon { font-size: 24px; margin-right: 8px; }
.logo-text { font-size: 22px; font-weight: 800; color: #f56c6c; letter-spacing: 1px; }

.menu { flex: 1; border: none !important; }
.header-right { display: flex; align-items: center; gap: 20px; }

.user-info { display: flex; align-items: center; gap: 10px; cursor: pointer; padding: 5px 12px; border-radius: 20px; transition: 0.3s; }
.user-info:hover { background: #f8fafc; }
.username { font-size: 14px; font-weight: 600; color: #475569; }

.main-content { background-color: #f8fafc; padding: 0; }

.banner-item { height: 520px; background-size: cover; background-position: center; display: flex; align-items: center; position: relative; }
.banner-overlay { position: absolute; inset: 0; background: linear-gradient(to right, rgba(0,0,0,0.7), transparent); }
.banner-content { position: relative; z-index: 2; color: #fff; margin-left: 10%; max-width: 600px; }
.banner-title { font-size: 48px; margin: 20px 0; font-weight: 800; }
.banner-desc { font-size: 18px; margin-bottom: 40px; opacity: 0.9; line-height: 1.6; }
.banner-actions { display: flex; gap: 20px; }

.statistics-wrapper { padding: 0 10%; margin-top: -60px; position: relative; z-index: 10; }
.statistics-card { background: #fff; border-radius: 20px; padding: 40px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }
.stat-box { text-align: center; display: flex; flex-direction: column; align-items: center; }
.stat-icon { font-size: 28px; color: #f56c6c; margin-bottom: 10px; }
.unit { font-size: 12px; color: #94a3b8; margin-top: 5px; }

.project-container { padding: 60px 10%; }
.section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 40px; }
.main-title { font-size: 28px; font-weight: 800; color: #1e293b; margin: 0; }
.sub-title { color: #64748b; margin: 10px 0 0; font-size: 15px; }

.modern-card { border-radius: 20px; border: none; transition: 0.4s; cursor: pointer; margin-bottom: 30px; }
.modern-card:hover { transform: translateY(-10px); box-shadow: 0 20px 40px rgba(0,0,0,0.1); }

.img-wrapper { position: relative; height: 230px; overflow: hidden; }
.project-image { width: 100%; height: 100%; object-fit: cover; transition: 0.5s; }
.modern-card:hover .project-image { transform: scale(1.1); }
.project-type-tag { position: absolute; top: 15px; left: 15px; z-index: 2; border: none; }
.image-mask { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; justify-content: center; align-items: center; color: #fff; opacity: 0; transition: 0.3s; font-weight: bold; }
.modern-card:hover .image-mask { opacity: 1; }

.card-info { padding: 25px; }
.project-title { font-size: 18px; font-weight: 700; color: #1e293b; margin: 0 0 12px; line-height: 1.4; height: 50px; }
.project-summary { font-size: 14px; color: #64748b; line-height: 1.6; height: 44px; margin-bottom: 25px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.progress-section { margin-bottom: 25px; }
.progress-text { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 10px; }
.percent-num { font-size: 24px; font-weight: 800; color: #f56c6c; }
.raised-num { font-size: 13px; color: #94a3b8; }

.card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 20px; border-top: 1px solid #f1f5f9; }
.target-box { display: flex; flex-direction: column; }
.target-box .label { font-size: 12px; color: #94a3b8; }
.target-box .value { font-size: 16px; font-weight: 700; color: #334155; }

.glow-btn { box-shadow: 0 6px 20px rgba(245, 108, 108, 0.3); font-weight: bold; }
.white-btn { border-color: #fff; color: #fff; }
.white-btn:hover { background: #fff; color: #333; }
.support-btn { padding: 10px 25px; font-weight: bold; }

.flex-grow { flex-grow: 1; }
</style>