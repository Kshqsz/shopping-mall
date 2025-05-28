package cn.edu.usts.cs2022.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {
    //订单id
    private Integer id;
    //订单编号
    private String orderNumber;
    //购买用户id
    private Integer userId;
    //产品id
    private Integer productId;
    //商家id
    private Integer merchantId;
    //购买状态
    private Integer status; // (已取消: -1, 待支付: 0, 已完成: 1)
    //创建时间
    private LocalDateTime createTime;
    //商品价格
    private BigDecimal price;
}
