<template>  
  <div>  
    <h1>用户管理</h1>  
    <el-table :data="userList" style="width: 100%">  
      <el-table-column prop="avatar" label="头像" width="100">  
        <template #default="scope">  
          <img :src="scope.row.avatar" alt="Avatar" class="user-avatar" />  
        </template>  
      </el-table-column>  
      <el-table-column prop="username" label="用户名" />  
      <el-table-column prop="phone" label="手机号" />  
      <el-table-column prop="createTime" label="创建时间" />  
      <el-table-column prop="updateTime" label="最后修改时间" />  
      <el-table-column prop="status" label="账号状态">  
        <template #default="scope">  
          <el-button  
            :type="scope.row.status == '1' ? 'success' : 'danger'"  
            @click="toggleStatus(scope.row)"  
          >  
            {{ scope.row.status == '1' ? '正常' : '禁用' }}  
          </el-button>  
        </template>  
      </el-table-column>  
    </el-table>  
  </div>  
</template>  

<script setup>  
import { ref } from 'vue';  
import { admingetalluser,adminChangeStatus} from '@/api/user';  
import { reactive, onMounted } from 'vue';  

  
const status = ref({  
  id: '',  
  status: '',  
});

// 切换用户状态  
const toggleStatus = (row) => {  
  // 根据当前状态切换为另一状态  
  const isConfirmed = window.confirm('确定要修改用户状态吗？');
  if (isConfirmed) {
    const userId = row.id;
  status.value.id = userId
  row.status = row.status == '1' ? '0' : '1';  
  status.value.status = row.status
  console.log(status.value.id)
  console.log(status.value.status)
  handleChangeStatus()
  ElMessage.success("操作成功")
    console.log('用户确认操作');
    // 执行相关操作的代码
  } else {
    // 用户点击了“取消”，不执行操作
    console.log('用户取消操作');
  }
  

};  
 const handleChangeStatus = async () => {  
  try {
    const res = await adminChangeStatus(status.value);
    console.log(res);
  } catch (error) {
    console.error('更新状态失败:', error);
    // 如果更新失败，可能需要将状态重置为原来的状态
    // 这里需要根据实际情况来决定是否需要重置状态
  }
}

// 创建响应式数组，用于存储用户信息
const userList = reactive([]);
// 获取所有用户方法实现  
const getAllUser = async () => {  
  try {  
    const res = await admingetalluser();  
    console.log('获取用户成功:', res.data.data);  
    // 使用 userList.splice 来确保响应式  
    userList.splice(0, userList.length, ...res.data.data); // 这个步骤确保 userList 被更新并且是响应式的  
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
};  

// 页面初始化后调用方法获取用户数据  
onMounted(() => {  
  getAllUser();  
});  
</script>  

<style scoped>  
.user-avatar {  
  width: 50px;  
  height: 50px;  
  border-radius: 50%;  
}  
</style>