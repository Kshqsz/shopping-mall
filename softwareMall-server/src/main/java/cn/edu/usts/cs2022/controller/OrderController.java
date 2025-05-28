package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.pojo.dto.OrderDTO;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.OrderVO;
import cn.edu.usts.cs2022.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 查询所有订单
     */
    @GetMapping("/all")
    public Result<List<Order>> selectAllOrder() {
        List<Order> list = orderService.selectAllOrder();
        return Result.success(list);
    }
    @PostMapping("/add")
    public Result<Order> add(@RequestBody OrderDTO orderDTO) {
        Order order =  orderService.add(orderDTO);
        return Result.success(order);
    }

    @PostMapping("/cancel/{number}")
    public Result cancel(@PathVariable("number") String orderNumber) {
        orderService.cancel(orderNumber);
        return Result.success();
    }

    @PostMapping("/pay/{orderNumber}")
    public Result pay(@PathVariable("orderNumber") String orderNumber) {
        orderService.pay(orderNumber);
        return Result.success();
    }

    /**
     * 查询带有价格的订单信息
     */
    @GetMapping()
    public Result<List<OrderVO>> selectOrderWithPrice(){
        List<OrderVO> list = orderService.selectOrderWithPrice();
        return Result.success(list);
    }

}
