import request from '@/utils/request.js'

export const firstCategoryList = () => {
    return request.get("/category/firstList");
}

export const secondCategoryList = (firstId) => {
    return request.get(`/category/second/${firstId}`)
}
