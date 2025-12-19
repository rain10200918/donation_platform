<template>
  <div class="settings-wrapper">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="never" class="menu-card">
          <div
              v-for="menu in menuOptions"
              :key="menu.id"
              :class="['menu-item', activeId === menu.id ? 'is-active' : '']"
              @click="activeId = menu.id"
          >
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.name }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card shadow="never" class="content-card">
          <div v-if="activeId === 1">
            <h3 class="section-title">筹款与财务规则</h3>
            <el-form label-position="top">
              <el-row :gutter="40">
                <el-col :span="12">
                  <el-form-item label="平台服务费比例 (%)">
                    <el-input-number v-model="config.fee" :precision="2" :step="0.1" :min="0" :max="10" />
                    <p class="input-tip">用户提现时，平台收取的运维成本费用</p>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="最低起提金额 (¥)">
                    <el-input-number v-model="config.minWithdraw" :min="1" />
                    <p class="input-tip">用户余额需达到此金额才可申请结算</p>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-divider />
              <el-form-item label="项目审核策略">
                <el-radio-group v-model="config.auditType">
                  <el-radio border label="auto">先发后审 (AI初筛)</el-radio>
                  <el-radio border label="manual">先审后发 (人工强制)</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>
          </div>

          <div v-if="activeId === 2">
            <h3 class="section-title">首页轮播图管理</h3>
            <el-table :data="banners" border>
              <el-table-column label="预览图" width="180">
                <template #default="{row}">
                  <el-image :src="row.url" class="banner-preview" />
                </template>
              </el-table-column>
              <el-table-column prop="title" label="标题" />
              <el-table-column prop="link" label="跳转链接" />
              <el-table-column label="操作" width="120">
                <template #default>
                  <el-button type="danger" link>移除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" icon="Plus" style="margin-top: 20px">添加新Banner</el-button>
          </div>

          <div v-if="activeId === 3">
            <h3 class="section-title">系统安全监控</h3>
            <el-form label-width="120px">
              <el-form-item label="系统维护模式">
                <el-switch v-model="config.maintenance" />
                <span class="status-desc">{{ config.maintenance ? '开启中（用户无法访问）' : '正常运行' }}</span>
              </el-form-item>
              <el-form-item label="单日捐款上限">
                <el-input v-model="config.dayLimit" style="width: 200px">
                  <template #append>元/人</template>
                </el-input>
              </el-form-item>
            </el-form>
          </div>

          <div class="footer-actions">
            <el-button type="primary" size="large" @click="handleSave">保存全局配置</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Setting, Picture, Lock, List } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const activeId = ref(1)

const menuOptions = [
  { id: 1, name: '业务规则', icon: 'Setting' },
  { id: 2, name: '内容装修', icon: 'Picture' },
  { id: 3, name: '安全设置', icon: 'Lock' }
]

const config = ref({
  fee: 0.60,
  minWithdraw: 100,
  auditType: 'manual',
  maintenance: false,
  dayLimit: 50000
})

const banners = ref([
  { title: '冬日温暖行动', url: 'https://img.freepik.com/free-vector/charity-concept-illustration_114360-6421.jpg', link: '/act/1' },
  { title: '大病救助通道', url: 'https://img.freepik.com/free-vector/volunteer-help-concept-illustration_114360-5014.jpg', link: '/act/2' }
])

const handleSave = () => {
  ElMessage.success('配置已生效，已实时同步至前端及缓存服务器')
}
</script>

<style scoped>
.settings-wrapper { padding: 24px; background: #f0f2f5; min-height: 100vh; }
.menu-card { border-radius: 8px; border: none; }
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 4px;
  transition: all 0.3s;
  color: #64748b;
}
.menu-item:hover { background: #f1f5f9; color: #3b82f6; }
.menu-item.is-active { background: #eff6ff; color: #3b82f6; font-weight: 600; }

.content-card { border-radius: 8px; border: none; min-height: 500px; position: relative; }
.section-title { margin-top: 0; margin-bottom: 24px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; font-size: 18px; color: #1e293b; }
.input-tip { font-size: 12px; color: #94a3b8; margin: 4px 0 0; }
.banner-preview { width: 140px; height: 60px; border-radius: 4px; }
.footer-actions { margin-top: 40px; padding-top: 20px; border-top: 1px solid #f1f5f9; text-align: right; }
.status-desc { margin-left: 12px; font-size: 14px; color: #64748b; }
</style>