package cn.edu.usts.cs2022.service;

import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.pojo.vo.CategoryVo;

import java.util.List;

public interface CategoryService {
    void add(String categoryName);


    /**
     * 获取所有分类
     * @return
     */
    List<Category> getAllCategory();

    /**
     * 添加新的分类
     * @param category
     */
    void insertCategory(Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Integer id);

    /**
     * 编辑分类
     * @param category
     */
    void updateCategory(Category category);


    String getCategoryById(Integer id);

    //获取一级分类
    List<CategoryVo> getFirstCategoryList();

    // 获取二级分类
    List<CategoryVo> getSecondCategoryList(Integer firstId);
}
