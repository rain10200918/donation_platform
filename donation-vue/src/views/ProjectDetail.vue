<template>
  <div class="detail-wrapper" v-loading="loading">
    <div class="detail-hero">
      <div class="hero-inner">
        <el-tag effect="dark" type="danger" class="category-tag">{{ getTypeText(project.projectType) }}</el-tag>
        <h1 class="project-title-xl">{{ project.title }}</h1>
        <div class="project-meta-top">
          <span class="meta-item"><el-icon><Calendar /></el-icon> 发起于 {{ formatDate(project.createTime) }}</span>
          <span class="meta-item"><el-icon><View /></el-icon> 2,405 人关注</span>
          <span class="meta-item"><el-icon><CircleCheck /></el-icon> 平台已核实申请人信息</span>
        </div>
      </div>
    </div>

    <div class="detail-content">
      <el-row :gutter="30">
        <el-col :span="16">
          <el-card class="content-card" shadow="never">
            <div class="section-box">
              <h3 class="section-title"><span class="title-icon"></span>求助详情</h3>
              <div class="story-content">
                {{ project.summary }}
              </div>
            </div>

            <el-divider />

            <div class="section-box">
              <h3 class="section-title">
                <span class="title-icon"></span>项目进展公示
                <el-tag size="small" type="info" round>{{ updates.length }} 条更新</el-tag>
              </h3>

              <el-empty v-if="updates.length === 0" description="求助人正在整理最新进展，请耐心等待" />

              <el-timeline v-else class="custom-timeline">
                <el-timeline-item
                    v-for="item in updates"
                    :key="item.id"
                    :timestamp="formatDate(item.createdAt)"
                    placement="top"
                    :type="item.updateType === 3 ? 'success' : 'danger'"
                >
                  <div class="update-node-card">
                    <div class="update-header">
                      <span class="update-badge" :class="'type-' + item.updateType">
                        {{ getUpdateTypeText(item.updateType) }}
                      </span>
                    </div>
                    <p class="update-text">{{ item.content }}</p>
                    <div class="update-imgs" v-if="item.images">
                      <el-image
                          v-for="(img, index) in item.images.split(',')"
                          :key="index"
                          :src="img"
                          :preview-src-list="item.images.split(',')"
                          class="update-thumb"
                          fit="cover"
                      />
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>

          <el-card class="comment-card" shadow="never">
            <template #header>留言鼓励 (12)</template>
            <div class="wish-item" v-for="i in 2" :key="i">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="wish-info">
                <div class="wish-user">爱心匿名网友 <span class="wish-time">2小时前</span></div>
                <div class="wish-text">加油！希望能早日渡过难关，世界因你而温暖。❤️</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <div class="sticky-sidebar">
            <el-card class="status-panel" shadow="always">
              <div class="fund-header">
                <span class="label">已募集爱心款</span>
                <div class="amount-large">
                  <span class="symbol">¥</span>
                  <span class="num">{{ project.raisedAmount?.toLocaleString() }}</span>
                </div>
              </div>

              <div class="progress-section">
                <el-progress
                    :percentage="calculatePercent(project.raisedAmount, project.targetAmount)"
                    :stroke-width="12"
                    color="#f56c6c"
                    :show-text="false"
                />
                <div class="prog-bottom">
                  <span>进度 {{ calculatePercent(project.raisedAmount, project.targetAmount) }}%</span>
                  <span>目标 ¥{{ project.targetAmount?.toLocaleString() }}</span>
                </div>
              </div>

              <div class="donor-stats">
                <div class="stat-cell">
                  <div class="val">128</div>
                  <div class="lab">捐助人次</div>
                </div>
                <el-divider direction="vertical" />
                <div class="stat-cell">
                  <div class="val">15</div>
                  <div class="lab">转发次数</div>
                </div>
              </div>

              <el-button type="danger" size="large" class="heart-btn" @click="handleDonate">
                立即捐助爱心
              </el-button>

              <div class="platform-promise">
                <el-icon><ShieldCheck /></el-icon>
                <span>爱心款项受第三方银行监管，安全透明</span>
              </div>
            </el-card>

            <el-card class="donor-list-card" shadow="never">
              <template #header>爱心贡献榜</template>
              <div class="donor-row" v-for="i in 3" :key="i">
                <div class="donor-rank">{{ i }}</div>
                <el-avatar :size="24" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                <span class="donor-name">爱心人士***</span>
                <span class="donor-amt">¥ 100</span>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Calendar, View, CircleCheck, Checked } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request.js'

const route = useRoute()
const loading = ref(false)
const project = ref({})
const updates = ref([])

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/project/detail/${route.params.id}`)
    if (res.code === "200") {
      project.value = res.data.project
      updates.value = res.data.updates
    }
  } catch (err) {
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

const calculatePercent = (raised, target) => {
  if (!target) return 0
  return Math.min(Math.floor((raised / target) * 100), 100)
}

const getTypeText = (t) => ({ 1: '个人求助', 2: '公益项目', 3: '紧急救助' }[t] || '其他')
const getUpdateTypeText = (t) => ({ 1: '进展说明', 2: '用款公示', 3: '结项总结' }[t] || '进展更新')
const formatDate = (s) => s ? s.split('T')[0] : '-'

const handleDonate = () => ElMessage.success('支付功能对接中，感谢您的关注！')

onMounted(fetchDetail)
</script>

<style scoped>
.detail-wrapper { background: #fdfdfd; min-height: 100vh; }

/* 顶部沉浸式背景 */
.detail-hero {
  background: linear-gradient(rgba(0,0,0,0.6), rgba(0,0,0,0.6)), url('https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?q=80&w=2070');
  background-size: cover;
  background-position: center;
  height: 280px;
  display: flex;
  align-items: center;
  padding: 0 15%;
  color: white;
}
.project-title-xl { font-size: 36px; margin: 15px 0; text-shadow: 0 2px 10px rgba(0,0,0,0.3); }
.project-meta-top { display: flex; gap: 25px; font-size: 14px; opacity: 0.9; }
.meta-item { display: flex; align-items: center; gap: 6px; }

.detail-content { padding: 40px 10%; margin-top: -50px; }

/* 左侧卡片美化 */
.content-card { border-radius: 16px; border: none; padding: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); }
.section-title { font-size: 20px; font-weight: bold; margin-bottom: 25px; display: flex; align-items: center; gap: 10px; }
.title-icon { width: 5px; height: 20px; background: #f56c6c; border-radius: 2px; }
.story-content { line-height: 2; color: #4b5563; font-size: 16px; white-space: pre-wrap; }

/* 时间轴美化 */
.update-node-card { background: #f8fafc; padding: 20px; border-radius: 12px; }
.update-badge { font-size: 12px; padding: 2px 10px; border-radius: 4px; color: white; margin-bottom: 10px; display: inline-block; }
.type-1 { background: #3b82f6; }
.type-2 { background: #f59e0b; }
.type-3 { background: #10b981; }
.update-text { font-size: 15px; line-height: 1.6; color: #374151; }
.update-thumb { width: 120px; height: 80px; border-radius: 8px; margin: 15px 10px 0 0; }

/* 右侧侧边栏 */
.sticky-sidebar { position: sticky; top: 20px; }
.status-panel { border-radius: 20px; border: none; padding: 10px; margin-bottom: 20px; }
.amount-large { color: #f56c6c; margin: 15px 0; }
.amount-large .symbol { font-size: 20px; font-weight: bold; margin-right: 5px; }
.amount-large .num { font-size: 42px; font-weight: 800; font-family: 'Helvetica'; }

.prog-bottom { display: flex; justify-content: space-between; font-size: 13px; color: #94a3b8; margin-top: 8px; }
.donor-stats { display: flex; justify-content: space-around; margin: 30px 0; }
.stat-cell { text-align: center; }
.stat-cell .val { font-size: 20px; font-weight: bold; color: #1e293b; }
.stat-cell .lab { font-size: 12px; color: #94a3b8; }

.heart-btn { width: 100%; height: 55px; font-size: 18px; border-radius: 12px; background: #f56c6c; border: none; transition: 0.3s; }
.heart-btn:hover { transform: scale(1.02); box-shadow: 0 10px 20px rgba(245,108,108,0.3); }

.platform-promise { display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 20px; font-size: 12px; color: #10b981; }

/* 捐助榜单 */
.donor-list-card { border-radius: 16px; border: none; }
.donor-row { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; font-size: 14px; }
.donor-rank { width: 20px; height: 20px; background: #f1f5f9; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 11px; }
.donor-amt { margin-left: auto; font-weight: bold; color: #475569; }

/* 留言区 */
.comment-card { margin-top: 30px; border-radius: 16px; border: none; }
.wish-item { display: flex; gap: 15px; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #f1f5f9; }
.wish-user { font-size: 14px; font-weight: bold; }
.wish-time { font-size: 12px; color: #94a3b8; margin-left: 10px; font-weight: normal; }
.wish-text { font-size: 14px; color: #4b5563; margin-top: 5px; }
</style>