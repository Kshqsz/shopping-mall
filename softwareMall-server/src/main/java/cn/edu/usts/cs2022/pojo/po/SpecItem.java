package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SpecItem {
    private Long id;
    private Long goodsId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer sales;
    private Date createTime;
    private Date updateTime;
}
