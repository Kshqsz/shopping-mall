<template>  
  <div class="category-management">  
    <h1>分类管理</h1>  
    <el-card class="new-category-card">  
      <h2>新增分类</h2>  
      <el-input v-model="newCategoryName" placeholder="输入分类名称" />  
      <el-button type="primary" @click="addCategory">添加</el-button>  
    </el-card>  

    <el-card class="category-list-card">  
      <h2>分类列表</h2>  
      <el-table :data="categories" style="width: 100%">  
        <el-table-column prop="id" label="ID" width="100" />  
        <el-table-column prop="name" label="名称" />  
        <el-table-column label="操作" width="200">  
          <template #default="scope">  
            <el-button type="primary" @click="editCategory(scope.row)">编辑</el-button>  
            <el-button type="danger" @click="deleteCategory(scope.row.id)">删除</el-button>  
          </template>  
        </el-table-column>  
      </el-table>  
    </el-card> 
    <!-- 编辑分类的对话框 -->
    <el-dialog title="编辑分类" v-model="isEditDialogVisible">
      <el-form :model="currentCategory">
        <el-form-item label="分类名称">
          <el-input v-model="currentCategory.name"></el-input>
        </el-form-item>
      </el-form>
      <span class="dialog-footer">
        <el-button @click="isEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCategory">更新</el-button>
      </span>
    </el-dialog>
     
  </div>  
</template>  

<script setup>
import { ref, reactive, onMounted } from 'vue';
import {admingetAllCategory , adminInsertCategory,admindeleteCategory ,adminUpdateCategory} from '../api/category'


// 查询所有分类信息
const categories = reactive([
]);
const getAllCategory = async () => {  
  try {  
    const res = await admingetAllCategory();  
    console.log('获取用户成功:', res.data.data);  
    // 确保响应式  
    categories.splice(0, categories.length, ...res.data.data);   
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
};  


// 添加分类的方法
const newCategoryName = ref('');
const addCategory = async () => {
  try{
    const res = await adminInsertCategory(newCategoryName.value);
    getAllCategory()
    console.log('添加用户成功:', res.data.data); 
  }catch (error) {  
    console.error('添加用户失败:', error);  
  }  
};


// 编辑分类的方法
const currentCategory = reactive({ id: '', name: '' });
const isEditDialogVisible = ref(false);
const editCategory = (category) => {
  currentCategory.id = category.id;
  currentCategory.name = category.name;
  isEditDialogVisible.value = true;
};

const updateCategory = async () => {
  try {
    const res = await adminUpdateCategory(currentCategory);
    getAllCategory();
    isEditDialogVisible.value = false;
    console.log('更新分类成功:', res.data);
  } catch (error) {
    console.error('更新分类失败:', error);
  }
};


// 删除分类的方法
const deleteCategory = async (id) => {
  const isConfirmed = window.confirm('确认删除该分类吗？');
  
  if (isConfirmed) {
    try{
    console.log(id)
    const res = await admindeleteCategory(id);
    getAllCategory()
    console.log('删除用户成功:', res.data); 
  }catch (error) {  
    console.error('删除用户失败:', error);  
  }  
  } else {
    // 用户点击了“取消”，不执行操作
    console.log('用户取消操作');
  }
  
};


onMounted(() => {  
  console.log("页面渲染")
  getAllCategory()
  console.log("获取数据成功")
}); 
</script>

<style>  
.category-management {  
  padding: 50px;  
}  

.new-category-card,  
.category-list-card {  
  margin-bottom: 20px;  
}  

.new-category-card .el-input,  
.new-category-card .el-button {  
  margin-top: 10px;  
}  
</style>