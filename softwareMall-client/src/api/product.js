import request from '@/utils/request.js'

// search api
export const productSearchService = (searchInfo) => {
    return request.get("/product/search", {params: {searchInfo: searchInfo }});
}

export const productGetByIdService = (id) => {
    return request.get(`/product/${id}`)
}

export const productGetAllService = (searchQuery) => {
    return request.post('/product/all',searchQuery)
}

export const productGetByIdsService = (ids) => {
    return request.post('/product/getByIds', ids);
}