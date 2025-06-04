package cn.edu.usts.cs2022.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ProductSimpleVo {
    private Integer id;
    private String name;
    private Double lowPrice;
    private Integer secondCategoryId;
    private String secondCategoryName;
    private Integer status;
    private Integer totalSales;
    private LocalDateTime updateTime;
}
