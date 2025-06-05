package cn.edu.usts.cs2022.pojo.vo.merchant;

import lombok.Data;

import java.time.LocalDateTime;

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
