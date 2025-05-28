<script setup>  
import { ref } from 'vue';  
import { useRouter } from 'vue-router';  
import { adminLoginService } from '@/api/admin';
import { useAdminStore} from '@/stores';
const form = ref({  
  username: '',  
  password: '',  
});  

const rules = {  
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],  
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],  
};  

const formRef = ref(null);  
const router = useRouter();  

const adminStore = useAdminStore()

const handleLogin = async () => {  
  const res = await adminLoginService(form.value)
  adminStore.setToken(res.data.data)
  
  console.log('1111')
  console.log(adminStore.token)
  
  router.push('/home'); 
  
}
</script>  

<template>  
  <div class="login-container">  
    <el-card class="login-card">  
      <h2>软件商城管理员登录登录</h2>  
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-position="top" 
        status-icon 
        @keydown.enter="handleLogin" 
      >  
        <el-form-item label="用户名" prop="username">  
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>  
        </el-form-item>  

        <el-form-item label="密码" prop="password">  
          <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>  
        </el-form-item>  

        <el-form-item >  
          <el-button type="primary" @click="handleLogin">登录</el-button>  
        </el-form-item>  
      </el-form>  
    </el-card>  
  </div>  
</template>  

<style>  
.login-container {  
  display: flex;  
  align-items: center;  
  justify-content: center;  
  height: 98vh;  
  background-size: cover; 
  background-position: center;  
  background-repeat: no-repeat; 
  background-image: url('../assets/img/bk.png');
}  

.login-card {  
  width: 400px;  
}  
</style>