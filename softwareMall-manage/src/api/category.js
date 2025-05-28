import request from '@/utils/request.js'

//查询分类
export const admingetAllCategory = () =>{
  return request.get("/category/all")
}

//添加分类
export const adminInsertCategory = (name) =>{
  return request.post("/category/add",{name})
}
//编辑分类
export const adminUpdateCategory = ({id,name}) =>{
  return request.put("/category",{id,name})
}


//删除分类
export const admindeleteCategory = (id) => {
  return request.delete(`/category/${id}`); // 注意这里的模板字符串
}

//根据id查询分类
export const getCategoryById = (id) =>{
  return request.get(`/category/${id}`)
}