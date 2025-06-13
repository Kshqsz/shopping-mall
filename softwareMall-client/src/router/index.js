import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: 
  [
    {
      path: '/login',
      component: () => import ('@/views/login/LoginPage.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      component: () => import('@/views/layout/LayoutContainer.vue'),
      redirect: '/homePage',
      meta: { requiresAuth: false },
      children: [
        { path: '/homePage', component: () => import('@/views/home/HomePage.vue'),meta: { requiresAuth: false }},
        { path: '/searchResult', component: () => import('@/views/home/SearchResult.vue'),meta: { requiresAuth: false }},
        { path: '/productDetail/:id', component: () => import('@/views/product/ProductDetail.vue'),meta: { requiresAuth: true }},
        { path: '/payment', component: () => import('@/views/order/Payment.vue'),meta: { requiresAuth: true }},
        { path: '/cart',component: () => import('@/views/cart/CartIndex.vue'),meta: { requiresAuth: true }},
        { path: '/orderDetail/:id',component: () => import('@/views/order/OrderDetail.vue'),meta: { requiresAuth: true }},
        { 
          path: '/userCenter', 
          component: () => import('@/views/user/UserCenter.vue'),
          redirect: '/profile',
          meta: { requiresAuth: true },
          children: [
            { path: '/profile', component: () => import('@/views/user/Profile.vue')},
            { path: '/orders', component: () => import('@/views/user/Orders.vue')},
            { path: '/favorites', component: () => import('@/views/user/Favorites.vue')},
            { path: '/address', component: () => import('@/views/user/AddressIndex.vue')}
          ]
        }
      ] 
    },
    {
      path: '/',
      redirect: '/home'
    },
  ],
})


// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  // 检查目标路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查用户是否已认证（检查token是否存在）
    if (userStore.token) {
      // 已认证用户允许访问
      next()
    } else {
      // 未认证用户重定向到登录页，并携带原始路径
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    // 不需要认证的路由直接放行
    next()
  }
})

export default router
