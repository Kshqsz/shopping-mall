package cn.edu.usts.cs2022.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {     // 订单ID
    private String orderNo;               // 订单编号(如：ORD20230615001)
    private Integer userId;                  // 用户ID
    private Integer productId;               // 商品ID
    private Integer specId;
    private BigDecimal productPrice;      // 商品单价
    private Integer quantity = 1;         // 购买数量
    private BigDecimal totalAmount;       // 订单总金额
    private Byte status = 0;              // 订单状态
    private Integer addressId;
}
