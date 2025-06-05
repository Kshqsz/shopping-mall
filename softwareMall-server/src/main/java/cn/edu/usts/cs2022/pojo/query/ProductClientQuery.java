package cn.edu.usts.cs2022.pojo.query;

import lombok.Data;

@Data
public class ProductClientQuery {
    private String name;
    private Integer level1Category;
    private Integer level2Category;
}
