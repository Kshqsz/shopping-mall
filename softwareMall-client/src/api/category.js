import request from '@/utils/request.js'

export const categoryGetAllService = () => {
    return request.get("/category/all");
}