import request from '@/utils/request.js'
//新增订单
export const orderAddService = (order) => {
    return request.post("/order/add", order)
}
//取消订单
export const orderCancelService = (orderId) => {
    return request.post(`/order/cancel/${orderId}`)
}

export const orderAllService = () => {
    return request.get("/order/all")
}

// 支付订单
export const orderPayService = (id) => {
    return request.post(`/order/pay/${id}`)
}

// 查询订单
export const getOrderList = (searchQuery) => {
    return request.post('/order/client/list',searchQuery)
}