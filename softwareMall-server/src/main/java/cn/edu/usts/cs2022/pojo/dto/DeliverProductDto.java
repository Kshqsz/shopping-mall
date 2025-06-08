package cn.edu.usts.cs2022.pojo.dto;

import lombok.Data;

@Data
public class DeliverProductDto {
    private Integer id;
    private String shippingCompany;
    private String shippingNumber;
}
