package cn.edu.usts.cs2022.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer userId;

    private Integer productId;

    private Integer merchantId;
}
