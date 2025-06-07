
package cn.edu.usts.cs2022.pojo.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer id;                     // 订单ID
    private String orderNo;               // 订单编号(如：ORD20230615001)
    private Integer userId;                  // 用户ID
    private String username;              // 用户名称
    private Integer merchantId;
    private String shopName;
    private Integer productId;               // 商品ID
    private String productName;           // 商品名称
    private String productImage;          // 商品主图URL
    private String productSpecs;          // 商品规格(如：颜色:红色;尺寸:XL)
    private Integer specId;
    private BigDecimal productPrice;      // 商品单价
    private Integer quantity = 1;         // 购买数量
    private BigDecimal totalAmount;       // 订单总金额
    private Integer status = 0;              // 订单状态
    private String receiverName;          // 收货人姓名
    private String receiverPhone;         // 收货人电话
    private String receiverProvince;      // 省份
    private String receiverCity;          // 城市
    private String receiverDistrict;      // 区县
    private String receiverDetail;       // 详细地址
    private String shippingCompany;       // 物流公司
    private String shippingNumber;        // 物流单号
    private LocalDateTime createTime;              // 订单创建时间
    private LocalDateTime paymentTime;             // 付款时间
    private LocalDateTime shippingTime;            // 发货时间
    private LocalDateTime completeTime;            // 完成时间
    private LocalDateTime cancelTime;              // 取消时间
    private LocalDateTime refundTime;              // 退款时间
    private LocalDateTime updateTime;              // 最后更新时间
}