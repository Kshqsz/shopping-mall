package cn.edu.usts.cs2022.mapper;


import cn.edu.usts.cs2022.pojo.dto.CartAddDTO;
import cn.edu.usts.cs2022.pojo.po.Cart;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.client.CartListVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    //查询购物车是否存在商品
    @Select("select * from cart where user_id = #{userId} and product_id = #{productId} and spec_id = #{specId}")
    Cart isExist(CartAddDTO cartAddDTO);
    //商品插入购物车
    @Insert("Insert into cart (user_id,product_id,spec_id,quantity) values (#{userId},#{productId},#{specId},#{quantity})")
    void addToCart(CartAddDTO cartAddDTO);

    @Update("update cart set quantity = #{quantity} where id = #{id}")
    void updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);


    List<CartListVo> selectCartList(@Param("userId") Integer userId);

    @Delete("delete from cart where id = #{id}")
    void deleteCartItem(Integer id);
}
