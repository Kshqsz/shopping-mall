package cn.edu.usts.cs2022.controller;


import cn.edu.usts.cs2022.mapper.CartMapper;
import cn.edu.usts.cs2022.pojo.dto.CartAddDTO;
import cn.edu.usts.cs2022.pojo.dto.OrderDTO;
import cn.edu.usts.cs2022.pojo.po.Cart;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.OrderVO;
import cn.edu.usts.cs2022.pojo.vo.client.CartListVo;
import cn.edu.usts.cs2022.service.OrderService;
import cn.edu.usts.cs2022.service.impl.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final OrderService orderService;
    //添加购物车
    @PostMapping()
    public Result addToCart(@RequestBody CartAddDTO cartAddDTO){
        cartService.addToCart(cartAddDTO);
        return Result.success();
    }

    //查询购物车列表
    @GetMapping("/{userId}")
    public Result<List<CartListVo>> getCartList(@PathVariable Integer userId){
        return Result.success(cartService.getCartList(userId));
    }

    //删除单个购物车商品
    @DeleteMapping()
    public Result deleteCart(@RequestParam List<Integer> ids){
        System.out.println(ids);
        cartMapper.deleteCartItem(ids);
        return Result.success();
    }

    @PostMapping("/update")
    public Result updateCart(@RequestBody Cart cart){
        cartMapper.updateQuantity(cart.getId(),cart.getQuantity());
        return Result.success();
    }
    //购物车结算
    @Transactional
    @PostMapping("/pay")
    public Result cartOrder(@RequestBody OrderDTO orderDTO){
        //根据商品id获取商户id
        Integer merchantId = cartMapper.selectMerchant(orderDTO);
        //清除购物车数据商品id和用户
        orderDTO.setMerchantId(merchantId);
        cartMapper.deleteCartByOrder(orderDTO);
        //创建订单数据
        Order order = orderService.add(orderDTO);
        return Result.success(order.getId());
    }

}
