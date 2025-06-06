package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer specId;
    private Integer quantity;
    private LocalDateTime createAt;
}
