package cn.edu.usts.cs2022.pojo.vo.client;

import lombok.Data;

@Data
public class CartListVo {
    //购物车表
    private Integer cartId;
    private Integer quantity;
    //商品表
    private Integer productId;
    private String productName;

    //规格表
    private String price;
    private Integer specId;
    private String specName;
    private String image;


}
