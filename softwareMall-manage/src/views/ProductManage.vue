<template>  
  <div>  
    <h1>软件产品管理</h1>  
    <div class="filters">  
      <div class="filter-item">  
        <span>排序方式:</span>  
        <el-select v-model="sortType">  
          <el-option label="默认排序" value=""></el-option>
          <el-option label="价格升序" value="priceAsc"></el-option>  
          <el-option label="价格降序" value="priceDesc"></el-option>  
        </el-select>  
      </div>  
      <div class="filter-item">  
        <span>软件查询:</span>  
        <el-input v-model="searchText" placeholder="" />  
        <el-button type="primary" @click="searchProducts">搜索</el-button>  
      </div>  
      <div class="filter-item">  
        <span>软件分类:</span>  
        <el-select v-model="selectedCategory" @change="filterByCategory">  
          <el-option label="全部" value=""></el-option>  
          <el-option v-for="category in categories" :key="category.name" :label="category.name" :value="category.name"></el-option>  
        </el-select>  
      </div>  
    </div>  
    <el-table :data="paginatedProducts" style="width: 100%">  
      <el-table-column prop="image" label="软件图片" width="100">  
        <template #default="scope">  
          <img :src="scope.row.image" alt="商品图片" class="product-image" />  
        </template>  
      </el-table-column>  
      <el-table-column prop="name" label="软件名称" />  
      <el-table-column prop="price" label="软件价格" />  
      <el-table-column prop="categoryName" label="软件分类" />  
      <el-table-column prop="merchantName" label="商家" />  
      <el-table-column label="操作">  
        <template #default="scope">  
          <el-button type="danger" @click="deleteProduct(scope.row)">强制下架</el-button>  
        </template>  
      </el-table-column>  
    </el-table>  
    <div class="pagination">  
      <el-pagination  
        v-model:current-page="currentPage"  
        v-model:page-size="pageSize"  
        :total="filteredProducts.length"  
        layout="prev, pager, next"  
        @current-change="handlePageChange"  
      />  
    </div>  
  </div>  
</template>  

<script setup>  
import { ref, reactive, computed ,onMounted} from 'vue';  
import {admingetAllCategory} from '../api/category'
import { getAllProduct ,updateStatus} from '../api/product'
import { getAllMerchant } from '../api/merchant'
import { ElMessage } from 'element-plus';


  
const productList = reactive([]);  
const categories = reactive([]);
const categorieMap = reactive([]);
const merchants = reactive([]);
const merchantMap = reactive([]);

// 查询所有商品数据
const getAllProductList = async () => {  
  try {  
    const res = await getAllProduct();  
    console.log('获取商品列表成功:', res.data.data);  

    // 确保响应式  
    const filteredProduct = res.data.data.filter(product => product.status == 1); 
     // 替换ID为名称 
    const productsWithNames = filteredProduct.map(product => {  
      return {  
        ...product,  
        categoryName: categorieMap[product.categoryId] || '未知分类',  
        merchantName: merchantMap[product.merchantId] || '未知商家'  
      };  
    });
    productList.splice(0, productList.length, ...productsWithNames); 
    console.log(productList)  
  } catch (error) {  
    console.error('获取商品列表失败:', error);  
  }  
};

//强制下架产品
const deleteProduct = async (row) =>{
  try {
    const res = await updateStatus({ id: row.id, status: -1 });
    console.log(res)
    getAllProductList()
    ElMessage.success("操作成功~")
  } catch (error) {
    console.error('下架失败', error);  
  }
}
// 获取分类列表  
const getAllCategory = async () => {  
  try {  
    const res = await admingetAllCategory();  
    console.log('获取用户成功:', res.data.data);  
    // 确保响应式  
    categories.splice(0, categories.length, ...res.data.data);
    categorieMap.splice(0, categorieMap.length, ...res.data.data);
    res.data.data.forEach(category => {  
      categorieMap[category.id] = category.name;   
    });     
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
};  

//获取商家信息
const getAllMerchants = async () => {
  try {  
    const res = await getAllMerchant();  
    console.log('获取商家成功:', res.data.data);  
    // 确保响应式  
    merchants.splice(0, merchants.length, ...res.data.data);
    merchantMap.splice(0, merchants.length, ...res.data.data);
    res.data.data.forEach(merchant => {  
      merchantMap[merchant.id] = merchant.username;   
    });     
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
}

// 价格排序  
const sortType = ref('');   

// 搜索的关键词  
const searchText = ref('');  

// 搜索的分类  
const selectedCategory = ref('');  

// 分页当前页码  
const currentPage = ref(1);  

// 一页展示数据数量  
const pageSize = ref(20);  

// 计算属性，用于根据搜索文本和选定类别过滤产品  
const filteredProducts = computed(() => {  
  // 创建产品列表的副本，以避免修改原始数组  
  let products = [...productList];  
  
  // 如果有搜索文本，按名称过滤产品  
  if (searchText.value) {  
    products = products.filter((product) =>  
      product.name.toLowerCase().includes(searchText.value.toLowerCase())  
    );  

  }  
  
  // 如果选择了类别，则按类别过滤产品  
  if (selectedCategory.value) {  
    products = products.filter((product) => product.categoryName == selectedCategory.value);  
  }  

  // 根据用户选择的排序类型对产品进行排序  
  if (sortType.value) { // 只有在用户选择了排序方式时才进行排序  
    products.sort((a, b) => {  
      return sortType.value === 'priceAsc' ? a.price - b.price : b.price - a.price;  
    });  
  }  
  
  // 返回过滤后的产品列表  
  return products;  
});  

// 计算属性，用于分页显示过滤后的产品  
const paginatedProducts = computed(() => {  
  // 根据当前页面和页面大小计算起始索引  
  const start = (currentPage.value - 1) * pageSize.value;  
  // 计算用于切片数组的结束索引  
  const end = start + pageSize.value;  
  // 返回当前页面的切片产品数组  
  return filteredProducts.value.slice(start, end);  
});  
// 函数，当搜索产品时将当前页面重置为1  
const searchProducts = () => {  
  currentPage.value = 1;  // 将当前页面重置为1  
};




onMounted(() => {  
  getAllCategory()
  getAllMerchants().then(() =>{getAllProductList()})
  
});
</script>  

<style scoped>  
.product-image {  
  width: 50px;  
  height: 50px;  
  object-fit: cover;  
}  
.filters {  
  display: flex;  
  justify-content: space-between;  
  align-items: center;  
  margin-bottom: 20px;  
}  
.filter-item {  
  display: flex;  
  align-items: center;  
}  
.filter-item span {  
  margin-right: 10px;  
  width: 130px;  
}  
.filter-item .el-input,  
.filter-item .el-select {  
  margin-right: 10px;  
}  
.pagination {  
  display: flex;  
  justify-content: center;  
  margin-top: 20px;  
}  
</style>