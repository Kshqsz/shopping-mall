<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores';
import {  updatePassword, userUpdateService,
         userGetByIdService} from '@/api/user'
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore()
const editDialogVisible = ref(false) // 控制修改个人信息对话框
const changePasswordDialogVisible = ref(false) // 控制修改密码对话框
const userInfo = ref(userStore.user)
const changePasswordFormRef = ref(null)
const updateAvatar = (response) => {
  editedUserInfo.value.avatar = response.data;
  ElMessage.success("上传头像成功~")
}
const editedUserInfo = ref({
  id: userInfo.value.id,
  avatar: userInfo.value.avatar,
  username: userInfo.value.username,
  phone: userInfo.value.phone
})
// 打开对话框方法
const openEditDialog = () => {
  editDialogVisible.value = true;
};

// 关闭对话框方法
const closeEditDialog = async () => {
  editDialogVisible.value = false;
};

const getUser = async () => {
  const res = await userGetByIdService(userInfo.value.id);
  userStore.setUser(res.data.data);
  userInfo.value = userStore.user;
}
const save = async () => {
  await userUpdateService(editedUserInfo.value);
  await getUser();
  ElMessage.success("修改个人信息成功~");
  editDialogVisible.value = false;
}

// 打开修改密码对话框
const openChangePasswordDialog = () => {
  changePasswordDialogVisible.value = true
}

const checkPasswordMatch = (rule, value, callback) => {
  if (value !== changePasswordForm.value.newPassword) {
    callback(new Error('确认密码与新密码不一致'))
  } else {
    callback()
  }
}
// 关闭修改密码对话框
const closeChangePasswordDialog = () => {
  changePasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  changePasswordDialogVisible.value = false
}


const changePasswordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const changePasswordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
  ],
  rePassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: checkPasswordMatch, trigger: 'blur' }
  ]
}

const handleSubmitChangePassword = async () =>{
  const res = await updatePassword(changePasswordForm.value)
  if(res.data.code === 0){
    ElMessage.success('修改成功，请重新登录')
    changePasswordDialogVisible.value = false
    userStore.removeToken();
    userStore.removeUsername();
    router.push('/login')
  }else{
    ElMessage.error(res.data.data)
  }
}


</script>

<template>
  <!-- 右侧内容 -->
  <el-card class="content" shadow="never">
    <!-- 用户信息和账号安全 -->
    <div class="user-info-container">
      <!-- 用户信息 -->
      <div class="user-info">
        <img class="avatar " :src="userInfo.avatar ? userInfo.avatar: 'https://via.placeholder.com/100' " alt="用户头像" />
        <div>
          <h2>{{ userInfo.username }}</h2>
          <p> 您好~ </p>
          <a href="#" class="edit-link" @click.prevent="openEditDialog">修改个人信息 &gt;</a>
        </div>
      </div>

      <!-- 账号安全信息 -->
      <div class="account-security">
        <p>绑定手机：{{ userInfo.phone }}</p>
      </div>
    </div>

    <hr style="margin-top: 40px; opacity: 0.4">
    
    
        <!-- 修改个人信息对话框 -->
      <el-dialog
      v-model="editDialogVisible"
      title="修改个人信息"
      width="500px"
      @close="closeEditDialog"
    >
      <el-form label-width="80px">
        <!-- 修改头像 -->
        <el-form-item label="头像">
          <el-upload
            action="/api/upload"
            :on-success="updateAvatar"
            :show-file-list="false"
          >
            <!-- 如果有头像，显示头像 -->
            <img v-if="editedUserInfo.avatar" :src="editedUserInfo.avatar" class="uploaded-avatar" />
            
            <!-- 如果没有头像，显示文字提示 -->
            <div v-else class="avatar-placeholder">
              点击上传头像
            </div>
          </el-upload>
        </el-form-item>
        <!-- 用户名（只读） -->
        <el-form-item label="用户名">
          <el-input v-model="editedUserInfo.username" placeholder="用户名" disabled></el-input>
        </el-form-item>

        <!-- 修改电话 -->
        <el-form-item label="手机号">
          <el-input v-model="editedUserInfo.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button type="text" @click="openChangePasswordDialog" style="margin-right: 200px; color: #409EFF;">修改密码</el-button>
        <el-button @click="closeEditDialog">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

   <!-- 修改密码对话框 -->
    <el-dialog
      v-model="changePasswordDialogVisible"
      title="修改密码"
      width="400px"
      @close="closeChangePasswordDialog"
    >
      <el-form :model="changePasswordForm" :rules="changePasswordRules" ref="changePasswordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="changePasswordForm.oldPassword" placeholder="请输入旧密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="changePasswordForm.newPassword" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="rePassword">
          <el-input type="password" v-model="changePasswordForm.confirmPassword" placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeChangePasswordDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmitChangePassword">确认</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.content {
  flex: 1;
  padding: 20px;
  
}

/* 用户信息与账号安全水平排列 */
.user-info-container {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  justify-content: space-between;
  padding-left: 120px;
}

.user-info {
  display: flex;
  align-items: center;
  
}

.user-info .avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-right: 20px;
  object-fit: cover;   /* 保持比例填充 */
}

.user-info h2 {
  font-size: 24px;
  margin: 0 0 10px;
}

.user-info p {
  margin: 0 0 10px;
  color: #888;
}

.edit-link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
}

.account-security p{
  margin: 5px 0;
  padding-right: 200px;
}

.security-level {
  color: #67c23a;
}

.stats {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 40px;
  margin-left: 100px;
}

.stat-item {
  cursor: pointer; 
  display: flex;
  align-items: center;
  width: calc(40% - 20px);
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 210px;
  padding-left: 20px;
}
.stat-item:hover {
  transform: scale(1.05);  /* 放大卡片 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);  /* 添加阴影效果 */
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 24px;
  margin-right: 10px;
}

.stat-info h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.stat-info p {
  margin: 5px 0;
  font-size: 24px;
  color: #333;
}

.stat-link {
  font-size: 14px;
  color: #409eff;
  text-decoration: none;
}
/* 设置头像为圆形 */
.uploaded-avatar {
  width: 100px;    /* 设置宽度 */
  height: 100px;   /* 设置高度 */
  border-radius: 50%;  /* 圆形 */
  object-fit: cover;   /* 保持比例填充 */
}

/* 头像空缺时的占位符样式 */
.avatar-placeholder {
  width: 100px;    /* 设置宽度 */
  height: 100px;   /* 设置高度 */
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;  /* 圆形 */
  background-color: #f5f5f5;  /* 背景色 */
  color: #606266;    /* 文字颜色 */
  font-size: 14px;    /* 字体大小 */
  cursor: pointer;   /* 鼠标为指针 */
  border: 1px dashed #dcdfe6;  /* 边框 */
  transition: background-color 0.3s; /* 背景颜色变化 */
}

/* 鼠标悬停时改变背景色 */
.avatar-placeholder:hover {
  background-color: #e6f7ff;
}
.stat-item i.far.fa-heart {
  border: none;
  box-shadow: none;
}

</style>
