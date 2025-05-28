<template>  
  <div>  
    <h1>软件上架审核</h1>  
    <el-table :data="paginatedProducts" style="width: 100%">  
      <el-table-column prop="name" label="软件名称" />  
      <el-table-column prop="price" label="软件价格" />  
      <el-table-column prop="categoryName" label="软件分类" />  
      <el-table-column prop="merchantName" label="商家" />  
      <el-table-column label="操作">  
        <template #default="scope">  
          <div style="display: flex; justify-content: center;">  
            <el-button type="success" @click="approveProduct(scope.row)">同意</el-button>  
            <el-button type="danger" @click="rejectProduct(scope.row)">拒绝</el-button>  
            <el-button type="primary" @click="showProductDetails(scope.row)">查看详情</el-button>  
          </div>  
        </template>  
      </el-table-column>  
    </el-table>  

    <el-dialog v-model="showDetailsDialog" title="商品详情" width="50%" center>  
    <div v-if="selectedProduct" class="product-details">  
      <div class="product-image-video-container">  
        
      <!-- 上方展示动态图片 -->  
      <img   
        :src="selectedProduct.image"  
          alt="产品图片"   
          class="product-image"   
        />  
         <!-- 视频区域 -->  
        <div class="video-container">  
          <video   
            class="product-video"   
            v-if="selectedProduct.video" 
            :src="selectedProduct.video" 
            controls   
          >  
            您的浏览器不支持 video 标签。  
          </video>  
          <div v-else>没有可播放的视频！</div>  
        </div>  
      </div>  
      <div class="product-info">  
        <h2>{{ selectedProduct.name }}</h2>  
        <p>{{ selectedProduct.description }}</p>  
        <p>价格: {{ selectedProduct.price }}</p>  
        <p>分类: {{ selectedProduct.categoryName }}</p>  
        <p>商家: {{ selectedProduct.merchantName }}</p>  
        <div class="download-button-container">  
          <el-button type="primary" @click="downloadProduct(selectedProduct)">  
            下载软件  
          </el-button>  
        </div>  
      </div>  
    </div>  
  </el-dialog>  
  </div>  
</template>  

<script setup>  



import { ref, reactive, computed ,onMounted} from 'vue';  
import {admingetAllCategory} from '../api/category'
import { getAllProduct } from '../api/product'
import { getAllMerchant } from '../api/merchant'
import { updateStatus } from '../api/product';
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
    const filteredProduct = res.data.data.filter(product => product.status == 0); 
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


const showDetailsDialog = ref(false);  
const selectedProduct = ref(null);  

const approveProduct = async (product) => {  
  // 审核通过逻辑  
  const isConfirmed = window.confirm('确定同意通过审核吗？');
  
  if (isConfirmed) {
    try {
    const res = await updateStatus({ id: product.id, status: 1 });
    console.log(res)
    getAllProductList()
    ElMessage.success("操作成功~")
  } catch (error) {
    console.error('下架失败', error);  
  } 
  } else {
    // 用户点击了“取消”，不执行操作
    console.log('用户取消操作');
  }
};


const rejectProduct = async (product) => {  
  // 审核失败逻辑  
  const isConfirmed = window.confirm('确定拒绝该审核吗？');
  
  if (isConfirmed) {
    try {
    const res = await updateStatus({ id: product.id, status: -1 });
    console.log(res)
    getAllProductList()
    ElMessage.success("操作成功~")
  } catch (error) {
    console.error('下架失败', error);  
  }  
  } else {
    // 用户点击了“取消”，不执行操作
    console.log('用户取消操作');
  }
  
};  

//展示商品详情
const showProductDetails = (product) => {  
  selectedProduct.value = product;  
  showDetailsDialog.value = true;  
};  


const downloadProduct = (product) => {  
    // 创建一个隐藏的 <a> 元素
  const url = product.source;
  const link = document.createElement('a');
  link.href = url;
  link.download = product.name; // 可选，设置下载文件的默认文件名
  //link.target = '_blank'; // 可选，避免干扰用户当前页面
  document.body.appendChild(link); // 将 <a> 添加到文档中
  link.click(); // 模拟点击下载
  document.body.removeChild(link); // 下载完成后移除 <a>
};  

onMounted(() => {
  getAllCategory()  
  getAllMerchants().then(() =>{getAllProductList()})
  // 在组件挂载时可以执行一些初始化操作  
});  
</script>  

<style scoped>  
.product-details {  
  display: flex;  
  justify-content: space-between;  
  align-items: center;  
}  

.product-image-container {  
  margin-bottom: 20px;  
}  

.product-image {  
  width: 100%;  
  height: 200px;  
  cursor: pointer;  
}  

.download-button-container {  
  margin-top: 20px;  
}  

.product-info {  
  flex-basis: 50%;  
  text-align: left;  
  padding-left: 20px;  
}  

.download-button-container {  
  display: flex;  
  justify-content: center;  
  margin-top: 20px;  
}  

</style>
<style scoped>  
.product-details {  
  display: flex;  
}  

.product-image-video-container {  
  display: flex;  
  flex-direction: column; /* 垂直排列 */  
  width: 50%; /* 左侧宽度 */  
}  

.product-image {  
  width: 300px; /* 使图片自适应 */  
  height: 200px; /* 高度自适应 */  
}  

.video-container {  
  margin-top: 10px; /* 图片与视频之间的间距 */  
}  

.product-video {  
  width: 300px ;/* 视频宽度自适应 */  
  height: 200px; /* 高度自适应 */  
}  

.product-info {  
  padding-left: 20px; /* 右侧信息的内边距 */  
}  
</style>  