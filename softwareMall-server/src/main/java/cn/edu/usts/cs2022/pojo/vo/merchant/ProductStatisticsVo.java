package cn.edu.usts.cs2022.pojo.vo.merchant;

import lombok.Data;

@Data
public class ProductStatisticsVo {
    private Integer total;
    private Integer onSale;
    private Integer  pending;
}
