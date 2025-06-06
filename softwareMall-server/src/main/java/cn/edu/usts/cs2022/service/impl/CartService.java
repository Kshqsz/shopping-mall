package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.CartMapper;
import cn.edu.usts.cs2022.pojo.dto.CartAddDTO;
import cn.edu.usts.cs2022.pojo.po.Cart;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.client.CartListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    CartMapper cartMapper;

    public void addToCart(CartAddDTO cartAddDTO) {
        //判断当前商品是否已经加入购物车（规格和商品id,userId都要校验）
        Cart cartItem =  cartMapper.isExist(cartAddDTO);
        //没有则加入购物车
        if (cartItem == null) {
            cartMapper.addToCart(cartAddDTO);
        }else {
            //有则将当前商品数量+1
            cartAddDTO.setQuantity(cartItem.getQuantity()+1);
            cartMapper.updateQuantity(cartItem.getId(),cartAddDTO.getQuantity());
        }


    }

    // 获取购物车列表
    public List<CartListVo> getCartList(Integer userId) {

        return cartMapper.selectCartList(userId);
    }
}
