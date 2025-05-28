package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.CategoryMapper;
import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    @Override
    public void add(String categoryName) {
        categoryMapper.add(categoryName);
    }

    /**
     * 获取全部分类
     * @return
     */
    @Override
    public List<Category> getAllCategory() {
        return categoryMapper.getAllCategory();
    }

    /**
     * 新增分类
     * @param category
     */
    @Override
    public void insertCategory(Category category) {
        categoryMapper.insertCategory(category);
    }

    /**
     * 删除分类
     * @param id
     */
    @Override
    public void deleteCategory(Integer id) {
        //TODO
        //添加删除限制
       /* adminMapper.selectProductByCategoryId(id)
        if()*/
        categoryMapper.deleteCategory(id);
    }

    /**
     * 编辑分类
     * @param category
     */
    @Override
    public void updateCategory(Category category) {
        categoryMapper.updatecategory(category);
    }

    /**
     * 根据id查询分类名称
     * @param id
     * @return
     */
    @Override
    public String getCategoryById(Integer id) {
        String name = categoryMapper.selectById(id);
        return name;
    }
}
