<script setup>
import { ref, onMounted, computed } from 'vue';
import { useUserStore } from '@/stores';
import { getOrderList,receive,orderCancelService, returnService} from '@/api/order';
import { useRouter } from 'vue-router';


const userStore = useUserStore();
const router = useRouter();
const user = userStore.user;
const orders = ref([]);
const filteredOrders = ref([]);
const currentPage = ref(1);
const pageSize = ref(6);
const keyword = ref('');
const activeTab = ref('all');

const detail = (id) => {
  router.push(`/orderDetail/${id}`);
};

// 分类标签配置
const tabs = ref([
  { label: '全部', value: 'all' },
  { label: '待付款', value: '0' },
  { label: '待发货', value: '1' },
  { label: '待收货', value: '2' },
  { label: '退款中', value: '6' },
  { label: '已退款', value: '7' },
]);

// 获取订单列表
const getOrders = async () => {
  try {
    const res = await getOrderList({
      userId: user.id,
      keyword: keyword.value,
      status: activeTab.value === 'all' ? null : activeTab.value
    });
    console.log(res.data.data)
    orders.value = res.data.data;
    applyFilter();
  } catch (error) {
    console.error('获取订单失败:', error);
  }
};

// 应用筛选
const applyFilter = () => {
  if (activeTab.value === 'all') {
    filteredOrders.value = orders.value;
  } else {
    filteredOrders.value = orders.value.filter(
      order => order.status === parseInt(activeTab.value)
    );
  }
  currentPage.value = 1; // 重置到第一页
};

// 处理标签切换
const handleTabChange = (tabValue) => {
  activeTab.value = tabValue;
  getOrders();
};

// 处理搜索
const handleSearch = () => {
  getOrders();
};

// 获取订单状态文本
const getOrderStatus = (status) => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    4: '已完成',
    5: '已取消',
    6: '退款中',
    7: '已退款',
  };
  return statusMap[status] || '未知状态';
};

// 分页改变
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 支付操作
const handlePay = (order) => {
  router.push({
    path: '/payment',
    query: {
      order: JSON.stringify(order),
    },
  });
};

const cancelOrder = (order) =>{
  // eslint-disable-next-line no-undef
  ElMessageBox.confirm(
    '确定要取消订单吗？',
    '确认取消',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderCancelService(order.id)
      if (res.data.code === 0) {
        // eslint-disable-next-line no-undef
        ElMessage.success('订单已取消')
         getOrders();
      } else {
        // eslint-disable-next-line no-undef
        ElMessage.error(res.message || '取消订单失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      // eslint-disable-next-line no-undef
      ElMessage.error('取消订单失败')
    }
  })
}

// 确认收货操作
const handleConfirmReceipt = async (order) => {
  await receive(order.id)
  getOrders();
  // eslint-disable-next-line no-undef
  ElMessage.success("收货成功")

};



// 获取当前页的订单数据
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredOrders.value.slice(start, end);
});

onMounted(() => {
  getOrders();
});

//处理退货申请
const returnRequest = async(id) =>{
  try {
    await ElMessageBox.confirm(
      '确定要申请退货退款吗？',
      '确认退货退款',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    await returnService(id)
    ElMessage.success('退货退款申请已提交')
    await getOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请退货退款失败:', error)
      ElMessage.error('申请退货退款失败')
    }
  }
}
</script>

<template>
  <el-card class="content" shadow="never">    
    <!-- 搜索框 -->
    <div class="search-section">
      <el-input
        v-model="keyword"
        placeholder="输入订单号或商品名称"
        clearable
        @clear="handleSearch"
        style="width: 300px; margin-right: 10px;"
      >
        <template #append>
          <el-button icon="el-icon-search" @click="handleSearch" />
        </template>
      </el-input>
    </div>
    
    <!-- 分类导航 -->
    <div class="category-nav">
      <el-radio-group v-model="activeTab" @change="handleTabChange">
        <el-radio-button 
          v-for="tab in tabs"
          :key="tab.value"
          :label="tab.value"
        >
          {{ tab.label }}
        </el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 订单列表 -->
    <el-table 
      :data="paginatedOrders" 
      
      style="width: 100%"
      v-if="filteredOrders.length > 0"
    >
      <el-table-column label="商品" :width="220" >
        <template #default="{ row }">
          <div class="product-info" @click="detail(row.id)">
            <img 
              :src="row.productImage" 
              alt="商品图片" 
              class="product-image"
            />
            <div class="product-meta">
              <div class="product-name">{{ row.productName }}</div>
              <div class="product-specs">{{ row.productSpecs }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column label="订单号" prop="orderNo" width="150" @click="detail(row.id)"/>
      
      <el-table-column label="商家" prop="shopName" width="120" />
      
      <el-table-column label="金额" width="110">
        <template #default="{ row }">
          <div class="price">￥{{ row.totalAmount.toFixed(2) }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <div :class="['status', `status-${row.status}`]">
            {{ getOrderStatus(row.status) }}
          </div>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="315">
        <template #default="{ row }">
          <!-- 根据状态显示不同操作按钮 -->
          <el-button 
            v-if="row.status === 0" 
            @click="handlePay(row)" 
            size="mini" 
            type="primary"
          >
            去支付
          </el-button>
          <el-button 
            v-if="row.status === 0" 
            @click="cancelOrder(row)" 
            size="mini" 
            type="danger"
          >
            取消订单
          </el-button>
        
          <el-button 
            v-if="row.status === 1 || row.status === 2 || row.status === 5 || row.status === 4 || row.status === 7 || row.status === 6" 
            @click="detail(row.id)"
            size="mini" 
            type="success"
          >
            查看详情
          </el-button>  
          <el-button 
            v-if="row.status === 2" 
            size="mini"
            @click="handleConfirmReceipt(row)"
          >
            确认收货
          </el-button>
          <el-button 
            v-if="row.status === 1 || row.status === 2" 
            @click="returnRequest(row.id)" 
            size="mini" 
            type="danger"
          >
            退货退款
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 空状态提示 -->
    <div v-else class="empty-state">
      <el-empty description="暂无相关订单" />
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="filteredOrders.length > pageSize">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="filteredOrders.length"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </el-card>
</template>

<style scoped>
.content {
  min-height: 500px;
}

.search-section {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.category-nav {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 10px;
}

.product-meta {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.product-specs {
  color: #999;
  font-size: 12px;
}

.price {
  font-weight: bold;
  color: #ff5000;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: inline-block;
}

.status-0 {
  background-color: #ffecef;
  color: #ff0036;
}

.status-1, .status-2 {
  background-color: #fff0e7;
  color: #ff6600;
}

.status-4 {
  background-color: #e8f8f0;
  color: #00a854;
}

.status-5 {
  background-color: #f8f8f8;
  color: #999;
}

.status-6 {
  background-color: #e0f0ff;
  color: #0052d9;
}

.status-7 {
  background-color: #f0f7ff;
  color: #3370ff;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
  color: #999;
}
</style>