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
                <p class="summary-text">{{ project.summary }}</p>

                <div v-if="project.picture" class="project-media">
                  <el-image
                      :src="project.picture"
                      :preview-src-list="[project.picture]"
                      fit="cover"
                      class="detail-main-img"
                  />
                </div>
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
            <template #header>
              <div class="comment-header-row">
                <span>留言鼓励 ({{ total }})</span>
                <el-button type="danger" link @click="scrollToInput">我要留言</el-button>
              </div>
            </template>

            <div class="publish-section" id="commentAnchor">
              <el-input
                  v-model="mainComment"
                  type="textarea"
                  :rows="3"
                  placeholder="写下你的爱心感言，为求助者加油打气..."
              />
              <div class="publish-footer">
                <div class="publish-ops">
                  <span class="tip ml-10">愿每一份善良都被温柔以待 ❤️</span>
                </div>
                <el-button type="danger" round @click="submitComment(0)">发表留言</el-button>
              </div>
            </div>

            <el-divider />

            <div v-loading="commentLoading">
              <div v-for="item in commentList" :key="item.id" class="wish-item-node">
                <el-avatar :size="38" :src="item.status === 0 ? defaultAvatar : (item.avatar || defaultAvatar)" />
                <div class="wish-info">
                  <div class="wish-user">
                    <span :class="{'is-donor-name': item.isDonor == 1}">
    {{ item.status == 0 ? '爱心匿名网友' : item.nickName }}
  </span>
                    <span v-if="item.isDonor == 1" class="donor-medal" title="爱心捐赠者">
    <el-icon><Medal /></el-icon>
    <span class="medal-text">爱心先行者</span>
  </span>
                    <el-tag v-if="item.isDonor === '1'" size="small" type="danger" effect="plain" class="donor-tag">爱心捐赠者</el-tag>
                    <span class="wish-time">{{ item.createTime }}</span>
                  </div>
                  <div class="wish-text">{{ item.content }}</div>
                  <div class="wish-ops">
                    <el-button link size="small" @click="handleReply(item)">回复</el-button>
                  </div>

                  <div v-if="item.children && item.children.length > 0" class="sub-reply-list">
                    <div v-for="child in item.children" :key="child.id" class="sub-reply-item">
                      <div class="sub-reply-user">
                       <span class="sub-name" :class="{'is-donor-name': child.isDonor == 1}">
    {{ child.status == 0 ? '匿名网友' : child.nickName }}
  </span>

                        <span v-if="child.isDonor == 1" class="donor-medal scale-08" title="爱心捐赠者">
    <el-icon><Medal /></el-icon>
    <span class="medal-text">爱心先行者</span>
  </span>

                        <span class="reply-label">回复</span>
                        <span class="target-name">@{{ child.replyNickName || '匿名网友' }}</span>
                        <span class="sub-time">{{ child.createTime }}</span>
                      </div>
                      <div class="sub-text">{{ child.content }}</div>
                      <div class="wish-ops">
                        <el-button link size="small" @click="handleReply(child, item.id)">回复</el-button>
                      </div>
                    </div>
                  </div>

                  <div v-if="activeReplyId === item.id || (activeParentId === item.id && activeReplyId !== null)" class="reply-input-box">
                    <el-input v-model="replyText" size="small" :placeholder="'回复 @' + activeReplyName + '...'" />
                    <div class="reply-btns">
                      <el-button size="small" link @click="cancelReply">取消</el-button>
                      <el-button size="small" type="danger" @click="submitComment(item.id)">提交</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="pagination-pane" v-if="total > 0">
                <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" layout="prev, pager, next" :total="total" background @current-change="fetchComments" />
              </div>
              <el-empty v-if="total === 0" description="暂无留言，快来留下第一句鼓励吧" />
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
                <el-icon><Lock /></el-icon> <span>爱心款项受第三方银行监管，安全透明</span>
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
import {Calendar, View, CircleCheck, Lock, Medal} from '@element-plus/icons-vue' // 修复
import { ElMessage } from 'element-plus'
import request from '../utils/request.js'

const route = useRoute()
const loading = ref(false)
const project = ref({})
const updates = ref([])

// --- 评论区逻辑变量 ---
const commentList = ref([])
const total = ref(0)
const commentLoading = ref(false)
const mainComment = ref('')
const replyText = ref('')
const currentStatus = ref(1) // 1实名 0匿名
const activeReplyId = ref(null)
const activeReplyUserId = ref(null)
const activeParentId = ref(null)
const activeReplyName = ref('')
const queryParams = ref({ pageNum: 1, pageSize: 5 })
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

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

// 获取留言
const fetchComments = async () => {
  commentLoading.value = true
  try {
    const res = await request.get(`/project-comment/list/${route.params.id}`, { params: queryParams.value })
    if (res.code === "200") {
      commentList.value = res.data.list
      total.value = res.data.total
    }
  } finally { commentLoading.value = false }
}

const handleReply = (target, rootId = null) => {
  activeReplyId.value = target.id
  activeReplyUserId.value = target.userId
  activeParentId.value = rootId || target.id
  activeReplyName.value = target.status === 0 ? '匿名网友' : target.nickName
}

const cancelReply = () => {
  activeReplyId.value = null; activeReplyUserId.value = null;
  activeParentId.value = null; replyText.value = ''; currentStatus.value = 1;
}

const submitComment = async (parentId) => {
  const isReply = parentId !== 0
  const content = isReply ? replyText.value : mainComment.value
  if (!content?.trim()) return ElMessage.warning('请输入内容')

  try {
    const res = await request.post('/project-comment/add', {
      projectId: route.params.id,
      content: content,
      parentId: isReply ? activeParentId.value : 0,
      replyUserId: isReply ? activeReplyUserId.value : 0,
      status: currentStatus.value
    })
    if (res.code === "200") {
      ElMessage.success('留言成功')
      mainComment.value = ''; currentStatus.value = 1;
      if (isReply) cancelReply()
      fetchComments()
    }
  } catch (e) { ElMessage.error('提交失败') }
}

const calculatePercent = (raised, target) => {
  if (!target) return 0
  return Math.min(Math.floor((raised / target) * 100), 100)
}

const getTypeText = (t) => ({ 1: '个人求助', 2: '公益项目', 3: '紧急救助' }[t] || '其他')
const getUpdateTypeText = (t) => ({ 1: '进展说明', 2: '用款公示', 3: '结项总结' }[t] || '进展更新')
const formatDate = (s) => s ? s.split('T')[0] : '-'
const handleDonate = () => ElMessage.success('支付功能对接中...')
const scrollToInput = () => document.getElementById('commentAnchor').scrollIntoView({ behavior: 'smooth' })

onMounted(() => {
  fetchDetail()
  fetchComments()
})
</script>

<style scoped>
/* 保持你原有的所有样式不变 */
.detail-wrapper { background: #fdfdfd; min-height: 100vh; }
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
.content-card { border-radius: 16px; border: none; padding: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); }
.section-title { font-size: 20px; font-weight: bold; margin-bottom: 25px; display: flex; align-items: center; gap: 10px; }
.title-icon { width: 5px; height: 20px; background: #f56c6c; border-radius: 2px; }
.story-content { line-height: 2; color: #4b5563; font-size: 16px; white-space: pre-wrap; }
.update-node-card { background: #f8fafc; padding: 20px; border-radius: 12px; }
.update-badge { font-size: 12px; padding: 2px 10px; border-radius: 4px; color: white; margin-bottom: 10px; display: inline-block; }
.type-1 { background: #3b82f6; }
.type-2 { background: #f59e0b; }
.type-3 { background: #10b981; }
.update-text { font-size: 15px; line-height: 1.6; color: #374151; }
.update-thumb { width: 120px; height: 80px; border-radius: 8px; margin: 15px 10px 0 0; }
.sticky-sidebar { position: sticky; top: 20px; }
.status-panel { border-radius: 20px; border: none; padding: 10px; margin-bottom: 20px; }
.amount-large { color: #f56c6c; margin: 15px 0; }
.amount-large .num { font-size: 42px; font-weight: 800; font-family: 'Helvetica'; }
.prog-bottom { display: flex; justify-content: space-between; font-size: 13px; color: #94a3b8; margin-top: 8px; }
.donor-stats { display: flex; justify-content: space-around; margin: 30px 0; }
.stat-cell { text-align: center; }
.stat-cell .val { font-size: 20px; font-weight: bold; color: #1e293b; }
.stat-cell .lab { font-size: 12px; color: #94a3b8; }
.heart-btn { width: 100%; height: 55px; font-size: 18px; border-radius: 12px; background: #f56c6c; border: none; transition: 0.3s; }
.platform-promise { display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 20px; font-size: 12px; color: #10b981; }
.donor-list-card { border-radius: 16px; border: none; }
.donor-row { display: flex; align-items: center; gap: 12px; margin-bottom: 15px; font-size: 14px; }
.donor-rank { width: 20px; height: 20px; background: #f1f5f9; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 11px; }
.donor-amt { margin-left: auto; font-weight: bold; color: #475569; }

/* 新增/优化：动态评论区样式 */
.comment-card { margin-top: 30px; border-radius: 16px; border: none; }
.comment-header-row { display: flex; justify-content: space-between; align-items: center; font-weight: bold; }
.publish-section { margin-top: 10px; }
.publish-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.tip { font-size: 12px; color: #94a3b8; font-style: italic; }
.ml-10 { margin-left: 10px; }

.wish-item-node { display: flex; gap: 15px; margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid #f1f5f9; }
.wish-info { flex: 1; }
.wish-user { font-size: 14px; font-weight: bold; display: flex; align-items: center; gap: 8px; }
.wish-time { font-size: 12px; color: #94a3b8; font-weight: normal; margin-left: auto; }
.donor-tag { transform: scale(0.8); }
.wish-text { font-size: 14px; color: #4b5563; margin-top: 8px; line-height: 1.6; }
.wish-ops { margin-top: 5px; }

/* 子回复 */
.sub-reply-list { background: #f8fafc; border-radius: 8px; padding: 12px; margin-top: 12px; }
.sub-reply-item { margin-bottom: 12px; border-bottom: 1px dashed #edf2f7; padding-bottom: 8px; }
.sub-reply-item:last-child { border: none; margin-bottom: 0; }
.sub-reply-user { font-size: 13px; font-weight: bold; margin-bottom: 4px; }
.reply-label { color: #94a3b8; margin: 0 5px; font-weight: normal; }
.target-name { color: #f56c6c; }
.sub-time { font-size: 12px; color: #cbd5e1; margin-left: 10px; font-weight: normal; }
.sub-text { font-size: 13px; color: #475569; }

.reply-input-box { margin-top: 10px; background: #fff; border: 1px solid #e2e8f0; border-radius: 6px; padding: 8px; }
.reply-btns { display: flex; justify-content: flex-end; align-items: center; gap: 10px; margin-top: 8px; }
.pagination-pane { margin-top: 20px; display: flex; justify-content: center; }
/* 勋章容器 */
.donor-medal {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: linear-gradient(135deg, #ffcf02 0%, #ff9a00 100%); /* 金色渐变 */
  color: #fff;
  padding: 1px 8px;
  border-radius: 12px 12px 12px 2px; /* 稍微不对称的切角更有设计感 */
  margin-left: 8px;
  box-shadow: 0 2px 4px rgba(255, 154, 0, 0.3);
  border: 1px solid #fff;
  transition: transform 0.3s ease;
  cursor: default;
}

/* 勋章图标 */
.donor-medal .el-icon {
  font-size: 14px;
  filter: drop-shadow(0 1px 1px rgba(0,0,0,0.2));
}

/* 勋章文字 */
.medal-text {
  font-size: 11px;
  font-weight: bold;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

/* 鼠标悬停时的微互动 */
.donor-medal:hover {
  transform: translateY(-2px) scale(1.05);
}
/* 补充子评论勋章的缩小效果 */
.donor-medal.scale-08 {
  transform: scale(0.85); /* 整体缩小 */
  transform-origin: left center; /* 缩放中心点靠左，防止产生左侧空白 */
  margin-left: 2px;
  margin-right: -4px; /* 补偿缩放后的间距 */
  vertical-align: middle;
}

/* 确保子评论头部也是 flex 布局，防止勋章换行 */
.sub-reply-user {
  display: flex;
  align-items: center;
  flex-wrap: wrap; /* 如果昵称太长允许换行 */
  gap: 4px;
}
/* 捐赠者特有的名字样式 */
.is-donor-name {
  color: #ef4444 !important; /* 漂亮的亮红色 */
  font-weight: 800 !important; /* 加粗显示 */
  text-shadow: 0 0 1px rgba(239, 68, 68, 0.1); /* 增加一点点发光感 */
}

/* 顺便优化一下匿名网友的样式，稍微淡化一点 */
.wish-user span:not(.is-donor-name) {
  color: #1f2937;
}

/* 如果是匿名且是捐赠者，依然保持红色 */
.is-donor-name {
  transition: color 0.3s ease;
}
.story-content {
  line-height: 1.8;
  color: #4b5563;
  font-size: 15px;
}

.summary-text {
  margin-bottom: 20px;
  white-space: pre-wrap; /* 保持后端文字的换行 */
}

.project-media {
  margin-top: 15px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.detail-main-img {
  width: 100%;
  display: block;
  transition: transform 0.3s;
}

.detail-main-img:hover {
  transform: scale(1.02); /* 鼠标悬停轻微放大效果 */
}
</style>