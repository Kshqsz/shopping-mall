package cn.edu.usts.cs2022.pojo.query;

import lombok.Data;

@Data
public class OrderMerchantQuery {
    private Integer merchantId;
    private String keyword;
    private Integer status;
}
