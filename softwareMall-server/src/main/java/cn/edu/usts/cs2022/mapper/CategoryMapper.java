package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.pojo.vo.CategoryVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    @Insert("insert into category(name) values (#{name})")
    void add(String name);

    /**
     * 查询所有分类
     * @return
     */
    @Select("select * from category")
    List<Category> getAllCategory();


    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category (name) values (#{name})")
    void insertCategory(Category category);

    /**
     * 删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteCategory(Integer id);

    /**
     * 编辑分类
     * @param category
     */
    @Update("update category set name = #{name} where id = #{id}")
    void updatecategory(Category category);

    @Select("select name from category where id = #{id}")
    String selectById(Integer id);

    // 获取一级分类
    @Select("select * from category where sort_order = 0")
    List<CategoryVo> getFirstCategoryList();

    //获取二级分类
    @Select("SELECT * FROM category where parent_category_id = #{firstId}")
    List<CategoryVo> getSecondCategoryList(Integer firstId);
}
