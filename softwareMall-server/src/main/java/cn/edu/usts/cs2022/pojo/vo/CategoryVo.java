package cn.edu.usts.cs2022.pojo.vo;

import lombok.Data;

@Data
public class CategoryVo {
    private Integer id;
    private String name;
    private Integer parentCategoryId;
    private Integer sortOrder;
}
