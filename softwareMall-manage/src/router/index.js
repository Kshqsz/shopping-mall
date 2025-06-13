import { createRouter, createWebHistory } from 'vue-router'

import login from '../views/LoginIndex.vue'
import Product from '../views/ProductManage.vue'
import Merchant from '../views/MerchantManger.vue'
import User from '../views/UserManager.vue'
import Home from '../views/HomeIndex.vue'
import Order from '../views/OrderIndex.vue'
import Category from '../views/CategoryIndex.vue'
import Count from '../views/CountIndex.vue'

const routes = [
  { path:'/', component:login },
  {  
    path: '/home',   
    component: Home,  
    redirect: '/home/count',
    children: [  
      { path: 'product', component: Product },  
      { path: 'merchant', component: Merchant },  
      { path: 'user', component: User },
      { path: 'order',component: Order },
      {path:'category',component:Category},
      {path:'count',component:Count},
    ],  
  },  

]
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
