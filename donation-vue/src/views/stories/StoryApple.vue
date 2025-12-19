<template>
  <div class="story-detail-page">
    <div class="story-banner" style="background-image: url('https://images.unsplash.com/photo-1516627145497-ae6968895b74?w=1200')">
      <div class="banner-mask">
        <div class="container">
          <el-button @click="router.back()" icon="ArrowLeft" round class="back-btn">返回</el-button>
          <h1>“小苹果”术后第一周，她终于露出了笑容</h1>
          <div class="meta">
            <span>发布时间：2025-10-15</span>
            <span>作者：公益志愿者-李华</span>
          </div>
        </div>
      </div>
    </div>

    <div class="story-content container">
      <el-row :gutter="40">
        <el-col :span="16">
          <div class="article">
            <h3>❤️ 一场跨越1000公里的爱心接力</h3>
            <p>那个被诊断为先天性心脏病的5岁女孩“小苹果”，在大家爱心的汇聚下顺利完成了手术。今天，她第一次走下了病床，在阳光下露出了久违的笑容。</p>

            <img src="https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800" class="content-img" />

            <h3>🔍 信息公开：资金拨付情况</h3>
            <div class="money-box">
              <p>本项目目前累计筹款 <strong>¥65,000</strong></p>
              <p>第一期手术费用拨款 <strong>¥45,000</strong> 已于 2025-10-10 直接汇入 <b>XX市第一人民医院</b> 对公账户。</p>
            </div>

            <p>在此，小苹果的父母委托我们向所有捐赠者致敬。是你们的10元、50元、100元，点亮了一个生命原本黯淡的星空。</p>
          </div>
        </el-col>
        <el-col :span="8">
          <el-card shadow="never" class="side-card">
            <h4>关联求助项目</h4>
            <div class="related-p" @click="router.push('/project/1')">
              <img src="https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=200" />
              <p>先心病女孩的重生之路</p>
              <el-tag size="small">募集中</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.story-detail-page { background: #fff; min-height: 100vh; }
.container { max-width: 1000px; margin: 0 auto; padding: 0 20px; }
.story-banner { height: 400px; background-size: cover; background-position: center; position: relative; }
.banner-mask { position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: flex-end; padding-bottom: 50px; color: white; }
.meta { margin-top: 15px; opacity: 0.8; font-size: 14px; }
.meta span { margin-right: 20px; }
.back-btn { position: absolute; top: 30px; left: 10%; }
.story-content { padding: 40px 0; line-height: 1.8; font-size: 16px; color: #333; }
.content-img { width: 100%; border-radius: 12px; margin: 25px 0; }
.money-box { background: #fef2f2; border-left: 4px solid #f56c6c; padding: 20px; margin: 20px 0; }
.money-box strong { color: #f56c6c; font-size: 20px; }
.side-card { border-radius: 12px; background: #f8fafc; }
.related-p { cursor: pointer; }
.related-p img { width: 100%; border-radius: 8px; }
</style>