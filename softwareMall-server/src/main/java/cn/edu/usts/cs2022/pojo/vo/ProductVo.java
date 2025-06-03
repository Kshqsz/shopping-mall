package cn.edu.usts.cs2022.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVo {
    Integer id;
    String name;
    BigDecimal price;
    Integer merchant_id;
    Integer merchant_name;
    Integer category_id;
    String category_name;
    String description;
    Integer status;
    String image;
    String video;
    Integer stock;

}
