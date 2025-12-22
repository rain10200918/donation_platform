<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card" shadow="never">
          <div class="user-profile">
            <div class="avatar-upload">
              <el-avatar :size="100" :src="adminForm.avatar || defaultAvatar" />
              <div class="upload-mask">
                <el-icon><Camera /></el-icon>
              </div>
            </div>
            <div class="user-info">
              <h3 class="user-name">{{ adminForm.nickName }}</h3>
              <p class="user-role">系统超级管理员</p>
            </div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-label">处理审批</div>
              <div class="stat-value">128</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">管理天数</div>
              <div class="stat-value">45</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="settings-card" shadow="never">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="basic">
              <el-form :model="adminForm" label-position="top" class="profile-form">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="管理员昵称">
                      <el-input v-model="adminForm.nickName" placeholder="请输入昵称" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="联系邮箱">
                      <el-input v-model="adminForm.email" placeholder="admin@example.com" />
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item label="个人简介">
                  <el-input v-model="adminForm.bio" type="textarea" :rows="4" placeholder="写点什么介绍自己..." />
                </el-form-item>
                <el-button type="primary" @click="handleUpdate">保存资料修改</el-button>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="安全设置" name="security">
              <div class="security-list">
                <div class="security-item">
                  <div class="sec-info">
                    <div class="sec-title">登录密码</div>
                    <div class="sec-desc">定期更换密码可以提高账号安全性</div>
                  </div>
                  <el-button link type="primary" @click="showPwdDialog = true">修改密码</el-button>
                </div>
                <div class="security-item">
                  <div class="sec-info">
                    <div class="sec-title">密保手机</div>
                    <div class="sec-desc">已绑定手机：188****8888</div>
                  </div>
                  <el-button link type="primary">更换手机</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showPwdDialog" title="重置登录密码" width="400px">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="旧密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePwd">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('basic')
const showPwdDialog = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const adminForm = ref({
  nickName: '',
  email: '',
  bio: '',
  avatar: ''
})

const pwdForm = ref({
  oldPassword: '',
  newPassword: ''
})

const fetchAdminInfo = async () => {
  const res = await request.get('/user/info')
  if (res.code === "200") adminForm.value = res.data
}

const handleUpdate = async () => {
  const res = await request.post('/user/update', adminForm.value)
  if (res.code === "200") ElMessage.success('资料更新成功')
}

const handleUpdatePwd = () => {
  ElMessage.success('密码已重置，请重新登录')
  showPwdDialog.value = false
}

onMounted(fetchAdminInfo)
</script>

<style scoped>
.profile-container { padding: 0; }
.user-card, .settings-card { border-radius: 12px; border: 1px solid #ebeef5; }

.user-profile { text-align: center; padding: 20px 0; border-bottom: 1px solid #f0f2f5; }
.avatar-upload {
  position: relative;
  display: inline-block;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}
.upload-mask {
  position: absolute; inset: 0; background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  color: #fff; opacity: 0; transition: 0.3s;
}
.avatar-upload:hover .upload-mask { opacity: 1; }

.user-name { margin: 15px 0 5px; font-size: 20px; color: #303133; }
.user-role { font-size: 14px; color: #909399; }

.user-stats { display: flex; padding: 20px 0; }
.stat-item { flex: 1; text-align: center; }
.stat-label { font-size: 13px; color: #909399; margin-bottom: 5px; }
.stat-value { font-size: 20px; font-weight: bold; color: #409eff; }

.security-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 0; border-bottom: 1px solid #f0f2f5;
}
.sec-title { font-size: 15px; font-weight: bold; color: #303133; }
.sec-desc { font-size: 13px; color: #909399; margin-top: 4px; }
</style>