<script setup>
import { userLoginService, userRegisterService } from '@/api/user'
import { ref, watch } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores';
import { useRouter } from 'vue-router';

const isRegister = ref(false)
const form = ref()

const register = async () => {
  await form.value.validate()
  await userRegisterService(formModel.value)
  ElMessage.success("注册成功~")
  isRegister.value = false
}

const userStore = useUserStore()
const router = useRouter()

const login = async () => {
  await form.value.validate()
  const res = await userLoginService(formModel.value)
  userStore.setToken(res.data.data.token)
  userStore.setUsername(res.data.data.user.username)
  userStore.setUser(res.data.data.user)
  ElMessage.success("登录成功~")
  router.push('/home')
}
// register
const formModel = ref({
  username: '',
  password: '',
  rePassword: ''
})


watch(isRegister, () => {
  if (!isRegister.value) {
    formModel.value.username = formModel.value.username;
    formModel.value.password = '';
  } else {
    formModel.value = {
    username: '',
    password: '',
    rePassword: ''
  }
  }
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }, // check when lost focus
    { min: 3, max: 10, message: '用户名必须为3~10位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{3,15}$/, message: '密码必须为3-15位非空字符', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{3,15}$/, message: '密码必须为3-15位非空字符', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formModel.value.password) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
}
</script>

<template>
  <el-row class="login-page">
    <el-col :span= 12 class="bg"></el-col>
    <el-col :span="6" :offset="3" class="form">
      <el-card class="box-card">
        <el-form :model="formModel" :rules="rules" ref="form" size="large" autocomplete="off" v-if="isRegister">
          <el-form-item>
            <h1>购物商城注册</h1>
          </el-form-item>
          <el-form-item prop="username">
            <el-input v-model="formModel.username" :prefix-icon="User" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="formModel.password" :prefix-icon="Lock" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item prop="rePassword">
            <el-input v-model="formModel.rePassword" :prefix-icon="Lock" type="password" placeholder="请输入再次密码" @keydown.enter="register"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button @click="register" class="button" type="primary" auto-insert-space> 注册 </el-button>
          </el-form-item>
          <el-form-item class="flex">
            <el-link type="info" :underline="false" @click="isRegister = false"> ← 返回 </el-link>
          </el-form-item>
        </el-form>
        <el-form :model="formModel" :rules="rules" ref="form" size="large" autocomplete="off" v-else>
          <el-form-item>
            <h1>购物商城登录</h1>
          </el-form-item>
          <el-form-item prop="username">
            <el-input v-model="formModel.username" :prefix-icon="User" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="formModel.password" name="password" :prefix-icon="Lock" type="password"
              placeholder="请输入密码" @keydown.enter="login">
            </el-input>
          </el-form-item>
          <el-form-item class="flex">
          </el-form-item>
          <el-form-item>
            <el-button @click="login" class="button" type="primary" auto-insert-space>登录</el-button>
          </el-form-item>
          <el-form-item class="flex">
            <el-link type="info" :underline="false" @click="isRegister = true"> 注册 → </el-link>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<style lang="scss" scoped>
.login-page {
  height: 98vh;
  background-color: #fff;

  .bg {
    background:
      //url('@/assets/logo2.png') no-repeat 60% center / 240px auto,
      url('@/assets/bk.png') no-repeat center / cover;
    border-radius: 0 20px 20px 0;
  }

  .form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    user-select: none;

    .title {
      margin: 0 auto;
    }

    .button {
      width: 100%;
    }

    .flex {
      width: 100%;
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>
