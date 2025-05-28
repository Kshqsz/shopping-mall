<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores';
import { userCountFavouriteService, userUpdatePasswordService, userUpdateService,
         userGetByIdService} from '@/api/user'
import { Tickets, Edit, Checked} from '@element-plus/icons-vue'

import {userCountOrderService} from '@/api/user'

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
    rePassword: ''
  }
  changePasswordDialogVisible.value = false
}


const changePasswordForm = ref({
  oldPassword: '',
  newPassword: '',
  rePassword: ''
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

const handleChangePassword = async () => {
  // 这里可以调用修改密码的API接口
  const passwordDTO = {
    password: changePasswordForm.value.newPassword,
    rePassword: changePasswordForm.value.rePassword
  };
  await userUpdatePasswordService(passwordDTO);
  await getUser();
  ElMessage.success('密码修改成功~')
  changePasswordForm.value = {
    oldPassword: '',
    newPassword: '',
    rePassword: ''
  }
  closeChangePasswordDialog()
}

const handleSubmitChangePassword = () => {
    // 校验旧密码是否正确
  if (changePasswordForm.value.oldPassword !== userInfo.value.password) {
    ElMessage.error('旧密码不正确');
    return; // 如果旧密码不正确，直接返回
  }
  // 表单校验
  const form = changePasswordFormRef.value
  form.validate((valid) => {
    if (valid) {
      handleChangePassword()
    } else {
      ElMessage.error('请填写正确的密码信息')
    }
  })
}

const stats = ref([
  { label: '我的订单', count: 0, path: '/orders' },
  { label: '待支付的订单', count: 0, path: '/orders' }, 
  { label: '喜欢的商品', count: 1, path: '/favorites' },
  { label: '已完成的订单', count: 0, path: '/orders' }, 
])
onMounted( async () => {
  await countFavourite();
  await countOrder();
})
const countOrder = async () =>{
  const userId = userInfo.value.id;
  const res_cancel = await userCountOrderService(
    {
      userId: userId,
      status: -1
    }
  );
  const res_ready = await userCountOrderService(
    {
      userId: userId,
      status: 0
    }
  );
  const res_finish= await userCountOrderService(
    {
      userId: userId,
      status: 1
    }
  );
  const cancel = res_cancel.data.data;
  const ready = res_ready.data.data;
  const finish = res_finish.data.data;
  const all = cancel + ready + finish; 
  stats.value[0].count = all;
  stats.value[1].count = ready;
  stats.value[3].count = finish;
}
const countFavourite = async () => {
  const userId = userInfo.value.id;
  const res = await userCountFavouriteService(userId);
  stats.value[2].count = res.data.data;
}
const router = useRouter()
// 获取统计项颜色
const getStatColor = (index) => {
  const colors = ['#FF7F00', '#409EFF', '#FF0000', '#67C23A']
  return colors[index % colors.length]
}
const goToDetail = (stat) => {
  router.push(stat.path)
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
    
    <!-- 数据统计 -->
    <div class="stats">
      <div
        v-for="(stat, index) in stats"
        :key="index"
        class="stat-item"
        @click="goToDetail(stat)"
      >
        <el-card class="stat-icon" shadow = "never":style="{ backgroundColor: stat.color || getStatColor(index) }" >
          <el-icon> 
            <Tickets v-if="index==0"></Tickets>
            <Edit v-if="index==1"></Edit>
            <Checked v-if="index==3"></Checked>
          </el-icon> 
          <i :class="'far fa-heart'" v-if="index===2" style="padding-bottom: 30px;"></i>
        </el-card>
        <div class="stat-info">
          <h3>{{ stat.label }}</h3>
          <p>{{ stat.count }}</p>
          <a href="#" class="stat-link">查看{{ stat.label }}</a>
        </div>
      </div>
    </div>
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
          <el-input type="password" v-model="changePasswordForm.rePassword" placeholder="请再次输入新密码"></el-input>
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
