<script setup>  
import { ref } from 'vue';  
import { useMerchantStore } from '@/stores';
// import { useRouter } from 'vue-router';
import { merchantUpdateService, merchantGetByIdService, merchantUpdatePasswordService } from '@/api/merchant'
import { ElMessage } from 'element-plus';
// const router = useRouter();
const merchantStore = useMerchantStore()
const userInfo = ref(merchantStore.merchant);

const showEditDialog = ref(false);  
const showChangePasswordDialog = ref(false);  

const passwordForm = ref({  
  oldPassword: '',  
  newPassword: '',  
  confirmPassword: '',  
});  
const editedUserInfo = ref({
  id: userInfo.value.id,
  avatar: userInfo.value.avatar,
  username: userInfo.value.username,
  phone: userInfo.value.phone,
  password: userInfo.value.password  // 添加password
});

const updateAvatar = (response) => {
  editedUserInfo.value.avatar = response.data;
  ElMessage.success("上传头像成功~");
}

const getMerchant = async () => {
  const res = await merchantGetByIdService(userInfo.value.id);
  merchantStore.setMerchant(res.data.data);
  userInfo.value = merchantStore.merchant;
}
const saveUserInfo = async () => {  
  await merchantUpdateService(editedUserInfo.value);
  await getMerchant();
  ElMessage.success("修改个人信息成功~")
  showEditDialog.value = false;  
};  

const changePassword = async () => {  
  // 校验原密码和新密码
  if (passwordForm.value.oldPassword !== userInfo.value.password) {
    ElMessage.error("原密码不正确!");
    return;
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error("新密码和确认密码不一致!");
    return;
  }  
  const passwordDTO = {
    password: passwordForm.value.newPassword,
    rePassword: passwordForm.value.confirmPassword
  };
  await merchantUpdatePasswordService(passwordDTO);
  // 执行修改密码的逻辑  
  ElMessage.success("修改密码成功~")
  showChangePasswordDialog.value = false;  
};  
</script>

<template>  
  <div class="merchant-info-container">  
    <el-card class="box-card">  
      <template #header>  
        <div class="card-header">  
          <span>个人信息</span>  
        </div>  
      </template>  
      <div class="user-profile">  
        <div class="avatar-wrapper">  
          <el-avatar :size="80" :src="userInfo.avatar" />  
        </div>  
        <div class="user-info">  
          <div class="username">用户名：{{ userInfo.username }}</div>  
          <div class="phone"> 手机号：{{ userInfo.phone }}</div>  
        </div>  
      </div>  
      <div class="actions">  
        <el-button type="primary" @click="showEditDialog = true">  
          编辑信息  
        </el-button>  
        <el-button type="primary" @click="showChangePasswordDialog = true">  
          修改密码  
        </el-button>  
      </div>  
    </el-card>  

    <el-dialog v-model="showEditDialog" title="编辑个人信息">  
      <el-form :model="editedUserInfo" label-width="80px">  
        <el-form-item label="用户名">  
          <el-input v-model="editedUserInfo.username" />  
        </el-form-item>  
        <el-form-item label="电话">  
          <el-input v-model="editedUserInfo.phone" />  
        </el-form-item>  
        <el-form-item label="头像">  
          <el-upload
            action="/api/upload"
            :on-success="updateAvatar"
            :show-file-list="false"
          >
            <img v-if="editedUserInfo.avatar" :src="editedUserInfo.avatar" class="uploaded-avatar" />
            <div v-else class="avatar-placeholder">点击上传头像</div>
          </el-upload>
        </el-form-item>  
      </el-form>  
      <template #footer>  
        <el-button @click="showEditDialog = false">取消</el-button>  
        <el-button type="primary" @click="saveUserInfo">保存</el-button>  
      </template>  
    </el-dialog>  

    <el-dialog v-model="showChangePasswordDialog" title="修改密码">  
      <el-form :model="passwordForm" label-width="80px">  
        <el-form-item label="原密码">  
          <el-input v-model="passwordForm.oldPassword" type="password" />  
        </el-form-item>  
        <el-form-item label="新密码">  
          <el-input v-model="passwordForm.newPassword" type="password" />  
        </el-form-item>  
        <el-form-item label="确认密码">  
          <el-input v-model="passwordForm.confirmPassword" type="password" />  
        </el-form-item>  
      </el-form>  
      <template #footer>  
        <el-button @click="showChangePasswordDialog = false">取消</el-button>  
        <el-button type="primary" @click="changePassword">保存</el-button>  
      </template>  
    </el-dialog>  
  </div>  
</template>  

<style scoped>  
.merchant-info-container {  
  display: flex;  
  justify-content: center;  
  align-items: center;  
  height: 95vh;  
  background-color: #f5f5f5;  
}  

.box-card {  
  width: 500px;  
  height: 300px;
}  

.card-header {  
  font-size: 18px;  
  font-weight: bold;  
}  

.user-profile {  
  display: flex;  
  align-items: center;  
  margin-bottom: 20px;  
}  

.avatar-wrapper {  
  margin-right: 20px;  
}  

.username {  
  font-size: 18px;  
  font-weight: bold;  
}  

.phone {  
  color: #666;  
}  

.actions {  
  display: flex;  
  justify-content: flex-end;  
}  

.actions button {  
  margin-left: 10px;  
}  

.uploaded-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f5f5f5;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  border: 1px dashed #dcdfe6;
  transition: background-color 0.3s;
}

.avatar-placeholder:hover {
  background-color: #e6f7ff;
}
</style>
