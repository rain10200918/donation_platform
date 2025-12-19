<template>
  <div class="story-detail-page">
    <div class="story-banner" style="background-image: url('https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=1200')">
      <div class="banner-mask">
        <div class="container">
          <el-button @click="router.back()" icon="ArrowLeft" round class="back-btn">返回</el-button>
          <h1 class="story-title">大山里的书声：希望小学图书角正式落成</h1>
          <div class="meta">
            <span><el-icon><Calendar /></el-icon> 发布时间：2025-11-20</span>
            <span><el-icon><User /></el-icon> 作者：平台官方运营</span>
          </div>
        </div>
      </div>
    </div>

    <div class="story-content container">
      <el-row :gutter="40">
        <el-col :span="16">
          <div class="article">
            <h3 class="section-h3">📖 知识是通往世界唯一的路</h3>
            <p>在这个海拔 2000 米的山村小学，孩子们最珍贵的礼物不是零食，而是一本带着油墨香气的画册。经过三个月的筹集，2000 本新书终于送达了。</p>

            <img src="https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=800" class="content-img" />

            <h3 class="section-h3">📊 财务透明：每一笔支出都有据可循</h3>
            <div class="money-box">
              <p>本项目目前累计筹款 <strong>¥12,800</strong></p>
              <p>所有款项已全部用于图书采购与物流，以下为资金使用明细：</p>
            </div>

            <el-table :data="tableData" border stripe class="custom-table">
              <el-table-column prop="item" label="支出项" />
              <el-table-column prop="amount" label="金额" />
              <el-table-column prop="note" label="说明" />
            </el-table>

            <p class="ending-text">
              虽然山路崎岖，但看到孩子们捧起书本那一刻眼里的光芒，我们深信，这些文字将带他们飞出大山，去往更远的世界。
            </p>
          </div>
        </el-col>

        <el-col :span="8">
          <div class="side-section">
            <div class="honor-roll">
              <h4>❤️ 爱心捐赠榜</h4>
              <ul>
                <li><span>张*华</span> <span class="amount">捐赠 ¥500</span></li>
                <li><span>匿名人士</span> <span class="amount">捐赠 ¥200</span></li>
                <li><span>某企业职工</span> <span class="amount">捐赠 ¥1000</span></li>
                <li><span>王*亮</span> <span class="amount">捐赠 ¥50</span></li>
              </ul>
              <el-divider />
              <p class="total-info">已有 156 位爱心人士参与</p>
            </div>
          </div>

          <el-card shadow="hover" class="related-card" @click="router.push('/projects')">
            <template #header>
              <div class="card-header"><span>关联求助项目</span></div>
            </template>
            <div class="related-p">
              <img src="https://images.unsplash.com/photo-1509062522246-3755977927d7?w=300" />
              <p>为大山孩子捐建图书室</p>
              <el-tag type="success" size="small">已圆满结束</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft, Calendar, User } from '@element-plus/icons-vue'

const router = useRouter()

const tableData = [
  { item: '采购正版图书', amount: '¥8,500', note: '含科普、文学、绘本类' },
  { item: '定制实木书柜', amount: '¥3,000', note: '共 5 组环保书架' },
  { item: '物流运输费', amount: '¥1,300', note: '偏远山区专线配送' },
]
</script>

<style scoped>
/* 核心布局样式 - 与 Story1 完全统一 */
.story-detail-page { background: #fff; min-height: 100vh; padding-bottom: 60px; }
.container { max-width: 1100px; margin: 0 auto; padding: 0 20px; }

/* Banner 效果 */
.story-banner {
  height: 450px;
  background-size: cover;
  background-position: center;
  position: relative;
}
.banner-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.8), rgba(0,0,0,0.2));
  display: flex;
  align-items: flex-end;
  padding-bottom: 60px;
  color: white;
}
.story-title { font-size: 36px; font-weight: 800; margin: 0 0 20px 0; }
.back-btn { position: absolute; top: 40px; background: rgba(255,255,255,0.2); border: none; color: #fff; }
.back-btn:hover { background: rgba(255,255,255,0.4); }
.meta { display: flex; gap: 30px; font-size: 15px; opacity: 0.9; }
.meta span { display: flex; align-items: center; gap: 5px; }

/* 文章正文 */
.story-content { margin-top: 40px; }
.article { line-height: 1.8; color: #374151; font-size: 17px; }
.section-h3 {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 40px 0 20px;
  padding-left: 12px;
  border-left: 5px solid #f56c6c;
}
.content-img { width: 100%; border-radius: 16px; margin: 30px 0; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
.ending-text { margin-top: 30px; padding: 20px; background: #f9fafb; border-radius: 8px; font-style: italic; }

/* 财务框样式 */
.money-box { background: #fef2f2; border: 1px dashed #fecaca; border-radius: 12px; padding: 25px; margin-bottom: 20px; }
.money-box strong { color: #f56c6c; font-size: 24px; margin: 0 5px; }
.custom-table { margin-bottom: 40px; border-radius: 8px; overflow: hidden; }

/* 侧边栏：荣誉榜 */
.side-section { margin-bottom: 30px; }
.honor-roll {
  background: #fffbeb;
  padding: 25px;
  border-radius: 16px;
  border: 1px solid #fde68a;
}
.honor-roll h4 { margin: 0 0 20px 0; color: #92400e; font-size: 18px; }
.honor-roll ul { list-style: none; padding: 0; margin: 0; }
.honor-roll li {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed #fcd34d;
  font-size: 15px;
  color: #78350f;
}
.amount { font-weight: bold; }
.total-info { text-align: center; color: #b45309; font-size: 13px; margin-top: 15px; }

/* 关联项目卡片 */
.related-card { border-radius: 16px; cursor: pointer; }
.card-header { font-weight: bold; color: #374151; }
.related-p img { width: 100%; height: 150px; object-fit: cover; border-radius: 8px; margin-bottom: 12px; }
.related-p p { font-weight: bold; margin: 0 0 10px 0; }
</style>