import request from '@/utils/request.js'

export const productGetAllByMerchantIdService = (merchantId) => {
    return request.get(`/product/all/${merchantId}`)
}

export const productUpdateService = (product) => {
    return request.put("/product", product)
}

// 查询所有商品

export const getAllProduct = () =>{
    return request.get("/product/all")
}

// 新增产品
export const addGoods = (productData) =>{
    return request.post("/product/add",productData)
}

//根据条件查询产品
export const selectGoods = (params) => {
    return request.post("/product/fetch",params )
}