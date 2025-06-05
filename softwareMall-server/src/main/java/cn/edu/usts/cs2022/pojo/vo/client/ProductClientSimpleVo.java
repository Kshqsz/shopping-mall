package cn.edu.usts.cs2022.pojo.vo.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductClientSimpleVo {
    private Long id;
    private String name;
    private Integer level1Category;
    private String level1CategoryName;
    private Integer level2Category;
    private String level2CategoryName;
    private String mainImage;
    private BigDecimal lowPrice;
    private Integer totalSales;
    private Integer merchantId;
    private String shopName;
}
