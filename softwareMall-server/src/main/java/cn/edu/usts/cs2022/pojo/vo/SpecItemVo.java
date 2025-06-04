package cn.edu.usts.cs2022.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpecItemVo {
    private Integer id;
    private Integer goods_id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer sales;
}
