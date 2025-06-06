package cn.edu.usts.cs2022.pojo.dto;

import lombok.Data;

@Data
public class CartAddDTO {
    private Integer userId;
    private Integer productId;
    private Integer specId;
    private Integer quantity;

}
