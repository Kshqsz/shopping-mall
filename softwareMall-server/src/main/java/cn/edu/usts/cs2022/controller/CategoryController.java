package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.pojo.dto.CategoryDTO;
import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.pojo.po.Product;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.CategoryVo;
import cn.edu.usts.cs2022.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result add(@RequestBody CategoryDTO categoryDTO) {
        String categoryName = categoryDTO.getName();
        categoryService.add(categoryName);
        return Result.success();
    }

    /**
     * 查询所有分类
     */
    @GetMapping("/all")
    public Result<List<Category>> getAllCategory() {
        List<Category> categoryList = categoryService.getAllCategory();
        return Result.success(categoryList);
    }


    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Integer id) {
        System.out.println(id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 编辑分类
     */
    @PutMapping()
    public Result updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 根据id查询分类
     */

    @GetMapping("/{id}")
    public Result getCategoryById(@PathVariable Integer id) {
        String name = categoryService.getCategoryById(id);
        return Result.success(name);
    }

    //获取第一级分类
    @GetMapping("/firstList")
    public Result<List<CategoryVo>> firstCategory(){
        return Result.success(categoryService.getFirstCategoryList());
    }

    // 获取二级分类
    @GetMapping("second/{firstId}")
    public Result<List<CategoryVo>> secondCategory(@PathVariable Integer firstId){
        return Result.success(categoryService.getSecondCategoryList(firstId));
    }

}
