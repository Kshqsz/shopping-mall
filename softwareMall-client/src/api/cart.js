import request from '@/utils/request.js'
//添加购物车
export const addToCart = (cartItem) => {
  return request.post("/cart",cartItem)
}

//查找购物车列表
export const getCartList = (userId) => {
  return request.get(`/cart/${userId}`)
}

//删除购物车
export const deleteCartItems = (ids) => {
  return request.delete(`/cart?ids=${ids}`)
}

//修改购物车商品数量
export const updateCart = (id,quantity) => {
  return request.post('/cart/update',{id,quantity})
}

//购物车下单
export const cartPay = (order) =>{
  return request.post('/cart/pay',order)
}