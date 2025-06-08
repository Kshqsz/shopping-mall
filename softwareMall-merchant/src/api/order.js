import request from '@/utils/request.js'

//查询所有订单信息
export const getOrderList = (searchQuery) =>{
  return request.post("/order/merchant/list",searchQuery)
}

//根据Id查询订单
export const getOrderById  = (id) =>{
    return request.get(`/order/${id}`)
} 

//处理发货
export const deliver = (dto) => {
  return request.post("/order/deliver",dto)
} 