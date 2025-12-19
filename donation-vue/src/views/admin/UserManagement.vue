<template>
  <div class="user-container">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8" v-for="item in userStats" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">{{ item.title }}</div>
            <div class="stat-value">{{ item.value }}</div>
          </div>
          <el-icon class="stat-icon" :style="{ color: item.iconColor }">
            <component :is="item.icon" />
          </el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header-wrapper">
          <div class="left-search">
            <el-input
                v-model="queryParams.nickName"
                placeholder="搜索用户昵称(支持模糊查询)"
                prefix-icon="Search"
                class="search-input"
                clearable
                @keyup.enter="handleSearch"
            />
            <el-select v-model="queryParams.status" placeholder="账号状态" clearable class="status-select">
              <el-option label="正常" value="1" />
              <el-option label="已封禁" value="0" />
            </el-select>
            <el-button type="primary" icon="Search" @click="handleSearch">查询</el-button>
          </div>
          <div class="right-ops">
            <el-button icon="Refresh" @click="loadData">刷新数据</el-button>
          </div>
        </div>
      </template>

      <el-table :data="userList" v-loading="loading" class="modern-table" stripe>
        <el-table-column label="用户信息" min-width="220">
          <template #default="{row}">
            <div class="user-info-cell">
              <el-avatar :size="42" :src="row.avatar || defaultAvatar" />
              <div class="user-detail">
                <div class="name-row">
                  <span class="nick-name">{{ row.nickName }}</span>
                  <el-tag v-if="row.role === '1'" size="small" type="danger" effect="dark" class="role-tag">管理员</el-tag>
                </div>
                <span class="user-id">UID: {{ row.id }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="mobile" label="联系电话" width="150" />

        <el-table-column label="身份标识" width="160">
          <template #default="{row}">
            <el-tag v-if="row.role === '1'" type="warning" size="small" >
               内部管理
            </el-tag>
            <el-tag v-else type="info" effect="plain" size="small">普通用户</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="注册时间" width="180">
          <template #default="{row}">
            <span class="time-col">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="账号状态" width="120">
          <template #default="{row}">
            <el-switch
                v-model="row.status"
                active-value="1"
                inactive-value="0"
                active-color="#13ce66"
                inactive-color="#ff4949"
                @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="160">
          <template #default="{row}">
            <el-button type="primary" link @click="viewDetail(row)">画像详情</el-button>
            <el-divider direction="vertical" />
            <el-button type="danger" link @click="handleDelete(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-pane">
        <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="用户综合画像" width="550px" class="custom-dialog">
      <div v-if="activeUser" class="user-profile">
        <div class="profile-header">
          <div class="avatar-group">
            <el-avatar :size="80" :src="activeUser.avatar || defaultAvatar" />
            <div class="header-info">
              <h3>{{ activeUser.nickName }}</h3>
              <p>{{ activeUser.email || '未绑定邮箱' }}</p>
            </div>
          </div>
        </div>

        <el-descriptions :column="2" border class="profile-desc">
          <el-descriptions-item label="用户编号">{{ activeUser.id }}</el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ activeUser.mobile }}</el-descriptions-item>
          <el-descriptions-item label="账号角色">
            {{ activeUser.role === '1' ? '超级管理员' : '普通用户' }}
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="activeUser.status === '1' ? 'success' : 'danger'">
              {{ activeUser.status === '1' ? '正常' : '封禁' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后登录" :span="2">
            {{ formatDate(activeUser.lastLoginAt) }}
            <span class="ip-text">({{ activeUser.lastLoginIp }})</span>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">
            {{ formatDate(activeUser.createTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="profile-footer-tip">
          * 登录IP显示 0:0:0:0... 为本地回环地址测试数据
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Medal, UserFilled, TrendCharts, StarFilled } from '@element-plus/icons-vue'

// 数据定义
const loading = ref(false)
const userList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const activeUser = ref(null)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const queryParams = ref({
  nickName: '',
  status: null
})

const userStats = ref([
  { title: '系统总用户', value: '0', icon: 'UserFilled', iconColor: '#409eff' },
  { title: '新增活跃度', value: '高', icon: 'TrendCharts', iconColor: '#67c23a' },
  { title: '核心管理层', value: '0', icon: 'StarFilled', iconColor: '#f1c40f' }
])

// 核心方法：加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/user/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        nickName: queryParams.value.nickName,
        status: queryParams.value.status
      }
    })
    if (res.code === "200") {
      userList.value = res.data.list
      total.value = res.data.total
      // 同步顶部看板数据
      userStats.value[0].value = res.data.total.toLocaleString()
      // 计算管理员人数
      const adminCount = res.data.list.filter(u => u.role === '1').length
      userStats.value[2].value = adminCount
    }
  } catch (err) {
    ElMessage.error('服务连接失败，请检查后端接口')
  } finally {
    loading.value = false
  }
}

// 事件处理
const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handlePageChange = (val) => {
  pageNum.value = val
  loadData()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleStatusChange = async (row) => {
  const targetStatus = row.status // "0" 或 "1"
  try {
    const res = await request.post('/admin/user/status', {
      userId: row.id,
      status: targetStatus
    })
    if(res.code === "200") {
      ElMessage({
        message: `用户 [${row.nickName}] 状态更新成功`,
        type: targetStatus === '1' ? 'success' : 'warning'
      })
    }
  } catch (err) {
    // 失败回滚
    row.status = row.status === "1" ? "0" : "1"
    ElMessage.error('更新失败，已回滚操作')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要永久注销用户 [${row.nickName}] 吗？此操作无法恢复！`, '高危操作确认', {
    confirmButtonText: '确定移除',
    cancelButtonText: '取消',
    type: 'error',
    buttonSize: 'default'
  }).then(async () => {
    try {
      await request.delete(`/admin/user/${row.id}`)
      ElMessage.success('用户已成功从系统中移除')
      loadData()
    } catch (err) {
      ElMessage.error('删除请求异常')
    }
  })
}

const viewDetail = (row) => {
  activeUser.value = row
  detailVisible.value = true
}

const formatDate = (s) => s ? s.replace('T', ' ').substring(0, 16) : '未登录记录'

onMounted(loadData)
</script>

<style scoped>
/* 容器布局 */
.user-container { padding: 24px; background-color: #f6f8fb; min-height: 100vh; }

/* 看板样式 */
.stat-row { margin-bottom: 24px; }
.stat-card { border: none; border-radius: 12px; transition: transform 0.3s; }
.stat-card:hover { transform: translateY(-4px); }
.stat-label { font-size: 14px; color: #64748b; margin-bottom: 8px; }
.stat-value { font-size: 30px; font-weight: 700; color: #1e293b; }
.stat-icon { position: absolute; right: 20px; top: 50%; transform: translateY(-50%); font-size: 50px; opacity: 0.12; }

/* 表格卡片 */
.table-card { border: none; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
.card-header-wrapper { display: flex; justify-content: space-between; align-items: center; }
.left-search { display: flex; gap: 12px; }
.search-input { width: 240px; }
.status-select { width: 130px; }

/* 用户信息列 */
.user-info-cell { display: flex; align-items: center; gap: 14px; }
.user-detail { display: flex; flex-direction: column; gap: 4px; }
.name-row { display: flex; align-items: center; gap: 8px; }
.nick-name { font-weight: 600; color: #334155; font-size: 15px; }
.user-id { font-size: 12px; color: #94a3b8; background: #f1f5f9; padding: 0 6px; border-radius: 4px; width: fit-content; }
.role-tag { font-size: 10px; padding: 0 4px; height: 18px; line-height: 18px; }

/* 详情弹窗 */
.profile-header { margin-bottom: 24px; padding: 10px; }
.avatar-group { display: flex; align-items: center; gap: 20px; }
.header-info h3 { margin: 0 0 6px 0; font-size: 20px; color: #1e293b; }
.header-info p { margin: 0; color: #64748b; font-size: 14px; }
.profile-desc { margin-top: 10px; }
.ip-text { font-size: 12px; color: #94a3b8; }
.profile-footer-tip { margin-top: 15px; font-size: 12px; color: #94a3b8; font-style: italic; }

.pagination-pane { margin-top: 24px; display: flex; justify-content: flex-end; }

/* 表格全局美化 */
:deep(.el-table__header th) {
  background-color: #f8fafc !important;
  color: #475569;
  font-weight: 700;
  height: 50px;
}
.time-col { color: #64748b; font-size: 13px; }
</style>