import request from '@/utils/request.js'



// 新增产品
export const addGoods = (productData) =>{
    return request.post("/product/add",productData)
}

//根据条件查询产品
export const selectGoods = (params) => {
    return request.post("/product/fetch",params )
}

// 查询商品详细信息
export const selectGoodDetail = (id) => {
    return request.get(`/product/detail/${id}`)
}

// 查询待修改商品信息
export const selectEditGood = (id) => {
    return request.get(`/product/editDetail/${id}`)
}

// 更新商品信息
export const updateGoods = (productData) =>{
    return request.post('/product/update',productData)
}

//修改商品状态
export const updateGoodsStatus = (statusDto) => {
    return request.post('/product/status',statusDto)
}

export const productStatis = () =>{
    return request.get('/product/statis')
}