import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: 
  [
    {
      path: '/login',
      component: () => import ('@/views/login/LoginPage.vue')
    },
    {
      path: '/home',
      component: () => import('@/views/layout/LayoutContainer.vue'),
      redirect: '/homePage',
      children: [
        { path: '/homePage', component: () => import('@/views/home/HomePage.vue')},
        { path: '/searchResult', component: () => import('@/views/home/SearchResult.vue')},
        { path: '/productDetail/:id', component: () => import('@/views/product/ProductDetail.vue')},
        { path: '/payment', component: () => import('@/views/order/Payment.vue')},
        { path: '/cart',component: () => import('@/views/cart/CartIndex.vue')},
        { path: '/orderDetail/:id',component: () => import('@/views/order/OrderDetail.vue')},
        { 
          path: '/userCenter', 
          component: () => import('@/views/user/UserCenter.vue'),
          redirect: '/profile',
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
      redirect: '/login'
    },
  ],
})

export default router
