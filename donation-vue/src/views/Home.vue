<template>
  <div class="common-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">❤️ 公益众筹平台</div>
        <el-menu mode="horizontal" :ellipsis="false" class="menu" default-active="1">
          <el-menu-item index="1" @click="router.push('/home')">首页</el-menu-item>
          <el-menu-item index="2" @click="router.push('/projects')">项目浏览</el-menu-item>
          <el-menu-item index="3">信息公开</el-menu-item>
          <div class="flex-grow" />
          <el-button type="primary" class="publish-btn" @click="handlePublish" style="margin-top: 13px">发起求助</el-button>
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar || defaultAvatar" />
              <span class="username">{{ userInfo.nickName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item>我的捐赠</el-dropdown-item>
                <el-dropdown-item @click="router.push('/my-projects')">我的求助</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-menu>
      </el-header>

      <el-main class="main-content">
        <el-carousel height="400px" motion-blur class="banner">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-item" :style="{ backgroundImage: `url(${item.img})` }">
              <div class="banner-text">
                <h2>{{ item.title }}</h2>
                <p>{{ item.desc }}</p>
                <el-button type="danger" size="large">立即帮助</el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>

        <div class="statistics">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic title="已筹集善款 (元)" :value="totalRaised" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="参与捐助人次" :value="totalDonations" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="已资助项目" :value="completedProjects" />
            </el-col>
          </el-row>
        </div>

        <div class="project-section">
          <h3 class="section-title">正在众筹</h3>
          <el-row :gutter="20">
            <el-col :span="8" v-for="p in projects" :key="p.id">
              <el-card class="project-card" :body-style="{ padding: '0px' }">
                <div class="image-wrapper">
                  <el-tag class="type-tag" :type="getTypeTag(p.projectType)">
                    {{ getTypeText(p.projectType) }}
                  </el-tag>
                  <img :src="getCover(p.projectType)" class="image" />
                </div>

                <div style="padding: 14px">
                  <span class="p-title">{{ p.title }}</span>
                  <div class="bottom">
                    <p class="p-desc">{{ p.summary }}</p>

                    <div class="progress-box">
                      <div class="progress-label">
                        <span>进度: {{ calculatePercent(p.raisedAmount, p.targetAmount) }}%</span>
                        <span>已筹: ¥{{ p.raisedAmount }}</span>
                      </div>
                      <el-progress
                          :percentage="calculatePercent(p.raisedAmount, p.targetAmount)"
                          :status="p.status === 2 ? 'success' : ''"
                          :show-text="false"
                      />
                    </div>

                    <div class="p-footer">
                      <span class="target">目标: ¥{{ p.targetAmount }}</span>
                      <el-button type="primary" size="small" plain @click="goDetail(p.id)">项目详情</el-button>
                    </div>
                  </div>
                </div>
              </el-card>
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
import request from '../utils/request.js' // 导入配置了拦截器的请求工具

const router = useRouter()

// 状态数据
const projects = ref([])
const totalRaised = ref(0)
const totalDonations = ref(0)
const completedProjects = ref(0)

// 静态轮播
const banners = ref([
  { id: 1, title: '山村小学图书室计划', desc: '让每一个孩子都能在书海翱翔', img: 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=1000' },
  { id: 2, title: '紧急救援：洪涝灾害灾后重建', desc: '众志成城，重建家园', img: 'https://images.unsplash.com/photo-1509099836639-18ba1795216d?q=80&w=1000' }
])
const userInfo = ref({
  nickName: '未登录', // 给个默认值，防止页面闪烁
  avatar: ''
})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 初始化加载数据
const loadHomeData = async () => {
  try {
    // 1. 获取项目分页列表 (对应你后端的 /project/list 接口)
    const projectRes = await request.get('/project/page', {
      params: { page: 1, size: 6 }
    })

    if (projectRes.code === "200") {
      projects.value = projectRes.data.list
    }

    // 2. 获取用户信息
    const userRes = await request.get('/user/info')
    if (userRes.code === "200") {
      userInfo.value = userRes.data
    }

    // 3. 获取统计数据 (如果你后端写了统计接口)
    // const statsRes = await request.get('/project/statistics')
    // if (statsRes.code === "200") {
    //   totalRaised.value = statsRes.data.totalRaised
    //   totalDonations.value = statsRes.data.totalCount
    // } else {
    // 后端没写前先保持模拟
    totalRaised.value = 1258000
    totalDonations.value = 45600
    completedProjects.value = 128
    // }

  } catch (err) {
    console.error('加载首页数据失败', err)
    // ElMessage.error('获取项目列表失败') // 屏蔽非必要报错，提升体验
  }
}

// 计算百分比
const calculatePercent = (raised, target) => {
  if (!target || target === 0) return 0
  const p = Math.floor((raised / target) * 100)
  return p > 100 ? 100 : p
}

// 根据 project_type 获取文本
const getTypeText = (type) => {
  const map = { 1: '个人求助', 2: '公益项目', 3: '紧急救助' }
  return map[type] || '其他'
}

// 根据 project_type 获取标签颜色
const getTypeTag = (type) => {
  const map = { 1: 'warning', 2: 'success', 3: 'danger' }
  return map[type] || 'info'
}

// 默认封面图生成器
const getCover = (type) => {
  const imgs = {
    1: 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=400',
    2: 'https://images.unsplash.com/photo-1581579438747-104c53d7fbc4?w=400',
    3: 'https://images.unsplash.com/photo-1509099836639-18ba1795216d?w=400'
  }
  return imgs[type] || 'https://via.placeholder.com/400x200'
}

const goDetail = (id) => {
  router.push(`/project/${id}`)
}

const handlePublish = async () => {
  const token = sessionStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录后再发起求助')
    router.push('/login')
    return
  }
  try {
    // 1. 调用你之前写好的获取认证详情接口
    const res = await request.get('/user-auth/detail')

    // 2. 根据 code 和 data 判断状态 (适配“不剥壳”模式)
    if (res.code === "200") {
      const auth = res.data

      // 情况 A: 根本没有认证记录
      if (!auth) {
        showAuthConfirm('您尚未进行实名认证，请先完成认证以确保项目真实性。')
        return
      }

      // 情况 B: 认证通过 (状态 1)
      if (auth.authStatus === 1) {
        router.push('/publish')
      }
      // 情况 C: 审核中 (状态 0)
      else if (auth.authStatus === 0) {
        ElMessage.warning('您的实名认证正在审核中，请稍后再试')
      }
      // 情况 D: 被驳回 (状态 2)
      else if (auth.authStatus === 2) {
        showAuthConfirm(`您的实名认证已被驳回（原因：${auth.rejectReason}），请修改后重新提交。`)
      }
    }
  } catch (err) {
    ElMessage.error('权限检查失败，请稍后重试')
  }
}

// 抽取一个公共的弹窗引导方法
const showAuthConfirm = (message) => {
  ElMessageBox.confirm(message, '身份校验', {
    confirmButtonText: '去认证',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    router.push('/user-auth') // 跳转到你写的认证页面
  }).catch(() => {})
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录并离开吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    sessionStorage.removeItem('token');
    localStorage.removeItem('token');
    ElMessage.success('已安全退出');
    router.replace('/login');
  }).catch(() => {});
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
/* 保持你之前的样式，新增几个微调 */
.header { display: flex; align-items: center; border-bottom: 1px solid #dcdfe6; background: #fff; padding: 0 50px; }
.logo { font-size: 22px; font-weight: bold; color: #409eff; margin-right: 40px; }
.menu { flex: 1; border-bottom: none; }
.flex-grow { flex-grow: 1; }
.user-info { display: flex; align-items: center; cursor: pointer; margin-left: 20px; outline: none; }
.username { margin-left: 8px; font-size: 14px; color: #606266; }
.main-content { background-color: #f0f2f5; padding: 0; }
.banner { margin-bottom: 30px; }
.banner-item { height: 400px; background-size: cover; background-position: center; display: flex; align-items: center; padding-left: 100px; }
.banner-text { background: rgba(255, 255, 255, 0.85); padding: 30px; border-radius: 8px; max-width: 400px; }
.statistics { background: #fff; padding: 40px; margin: -50px 50px 30px; border-radius: 8px; position: relative; z-index: 10; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.project-section { padding: 20px 50px 50px; }
.section-title { font-size: 24px; margin-bottom: 20px; color: #303133; }
.project-card { transition: transform 0.3s; margin-bottom: 20px; border-radius: 10px; overflow: hidden; }
.project-card:hover { transform: translateY(-5px); cursor: pointer; }
.image-wrapper { position: relative; }
.type-tag { position: absolute; top: 10px; left: 10px; z-index: 5; }
.image { width: 100%; height: 200px; object-fit: cover; }
.p-title { font-weight: bold; font-size: 17px; color: #303133; }
.p-desc { font-size: 13px; color: #909399; margin: 10px 0; height: 40px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.progress-box { margin: 15px 0; }
.progress-label { display: flex; justify-content: space-between; font-size: 12px; color: #666; margin-bottom: 5px; }
.p-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; padding-top: 10px; border-top: 1px dashed #ebeef5; }
.target { color: #f56c6c; font-weight: bold; font-size: 14px; }
</style>