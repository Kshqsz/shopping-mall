
package cn.edu.usts.cs2022.pojo.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer id;

    private String orderNumber;

    private Integer userId;

    private Integer productId;

    private Integer merchantId;

    private Integer status; // (已取消: -1, 待支付: 0, 已完成: 1)

    private LocalDateTime createTime;
}