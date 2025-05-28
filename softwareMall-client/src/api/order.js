import request from '@/utils/request.js'

export const orderAddService = ({ userId, productId, merchantId}) => {
    return request.post("/order/add", {userId, productId, merchantId})
}

export const orderCancelService = (orderNumber) => {
    return request.post(`/order/cancel/${orderNumber}`)
}

export const orderAllService = () => {
    return request.get("/order/all")
}

export const orderPayService = (orderNumber) => {
    return request.post(`/order/pay/${orderNumber}`)
}