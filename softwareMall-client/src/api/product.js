import request from '@/utils/request.js'

// search api
export const productSearchService = (searchInfo) => {
    return request.get("/product/search", {params: {searchInfo: searchInfo }});
}

export const productGetByIdService = (id) => {
    return request.get(`/product/${id}`)
}

export const productGetAllService = () => {
    return request.get('/product/all')
}

export const productGetByIdsService = (ids) => {
    return request.post('/product/getByIds', ids);
}