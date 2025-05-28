<template>  
  <div>  
    <h1>商家管理</h1>  
    <el-table :data="merchantList" style="width: 100%">  
      <el-table-column prop="avatar" label="头像" width="100">  
        <template #default="scope">  
          <img :src="scope.row.avatar" alt="Avatar" class="merchant-avatar" />  
        </template>  
      </el-table-column>  
      <el-table-column prop="username" label="商家名称" />  
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
      <el-table-column label="上架商品">  
        <template #default="scope">  
          <el-button type="primary" @click="showProductModal(scope.row)">  
            查看商品  
          </el-button>  
        </template>  
      </el-table-column>  
    </el-table>  

    <!-- 商品展示模态框 -->  
    <el-dialog v-model="showModal" title="上架商品" width="80%">  
      <div v-if="selectedMerchant">  
        <h3>{{ selectedMerchant }}的上架商品</h3>  
        <div>  
          <el-form :inline="true" class="demo-form-inline">  
            <!-- <el-form-item label="排序方式">  
              <el-select v-model="sortBy" placeholder="请选择">  
                <el-option label="价格升序" value="priceAsc" />  
                <el-option label="价格降序" value="priceDesc" />  
              </el-select>  
            </el-form-item>   -->
            <!-- <el-form-item label="商品分类">  
              <el-select v-model="selectedCategory" placeholder="请选择">  
                <el-option label="全部" value="" />  
                <el-option  
                  v-for="category in categories"  
                  :key="category.name"  
                  :label="category.name"  
                  :value="category.name"  
                />  
              </el-select>  
            </el-form-item>  --> 
          </el-form>  
        </div>  
        <el-table :data="product_M.value" style="width: 100%">  
          <el-table-column prop="image" label="图片" width="100">  
            <template #default="scope">  
              <img :src="scope.row.image" alt="Product Image" width="50" />  
            </template>  
          </el-table-column>  
          <el-table-column prop="name" label="商品名称" />  
          <el-table-column prop="description" label="商品描述" />  
          <el-table-column prop="price" label="价格" />  
          <el-table-column prop="source" label="商品链接">  
            <template #default="scope">  
              <a :href="scope.row.link" target="_blank">{{ scope.row.source }}</a>  
            </template>   
          </el-table-column>  
          <el-table-column prop="categoryName" label="商品分类" />  
        </el-table>  
      </div>  
      <template #footer>  
        <span class="dialog-footer">  
          <el-button @click="showModal = false">关闭</el-button>  
        </span>  
      </template>  
    </el-dialog>  
  </div>  
</template>  

<script setup>  
import { reactive, ref, onMounted ,computed} from 'vue';  
import { getAllMerchant ,ChangeMerchantStatus} from '../api/merchant'

import {admingetAllCategory} from '../api/category'
import { getAllProduct } from '../api/product'




//修改商家账号状态
const status = ref({  
  id: '',  
  status: '',  
});
const toggleStatus = (row) => {  
  const isConfirmed = window.confirm('确定要修改该商家状态吗？');
  
  if (isConfirmed) {
    status.value.id = row.id
  row.status = row.status == '1' ? '0' : '1';  
  status.value.status = row.status
  console.log(status.value.id)
  console.log(status.value.status)
  ChangeMerchantStatus(status.value)
  } else {
    // 用户点击了“取消”，不执行操作
    console.log('用户取消操作');
  }
  

};  




const showModal = ref(false);  
const selectedMerchant = ref(null);  

 


//获取商品列表
const product_M = ([])
//点击打开商品列表
const showProductModal = (merchant) => {  
  console.log(merchant.username)
  const merchantId = merchant.id
  console.log(merchantId)
  selectedMerchant.value = merchant.username; 
  console.log(paginatedProducts.value) 
  product_M.value = paginatedProducts.value.filter(product => product.merchantId == merchantId);
  console.log(product_M.value)

  showModal.value = true;  
};  


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


//获取所有商家列表实现
const merchantList = reactive([]);    
const getMerchant = async () => {  
  try {  
    const res = await getAllMerchant();  
    console.log('获取用户成功:', res.data.data);  
    // 使用 merchantList.splice 来确保响应式  
    merchantList.splice(0, merchantList.length, ...res.data.data);
    merchantMap.splice(0, merchants.length, ...res.data.data);
    res.data.data.forEach(merchant => {  
      merchantMap[merchant.id] = merchant.username;   
    });  // 这个步骤确保 userList 被更新并且是响应式的  
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
};  

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


onMounted(() => {  
  getMerchant()
  getAllCategory()  
  console.log("查询商家列表成功")
  getAllProductList()
});  
</script>  

<style scoped>  
.merchant-avatar {  
  width: 50px;  
  height: 50px;  
  border-radius: 50%;  
}  
</style>