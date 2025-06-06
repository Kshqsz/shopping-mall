package cn.edu.usts.cs2022.controller;


import cn.edu.usts.cs2022.mapper.CartMapper;
import cn.edu.usts.cs2022.pojo.dto.CartAddDTO;
import cn.edu.usts.cs2022.pojo.po.Cart;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.client.CartListVo;
import cn.edu.usts.cs2022.service.impl.CartService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

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
    @DeleteMapping("/{id}")
    public Result deleteCart(@PathVariable Integer id){
        cartMapper.deleteCartItem(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result updateCart(@RequestBody Cart cart){
        cartMapper.updateQuantity(cart.getId(),cart.getQuantity());
        return Result.success();
    }
}
