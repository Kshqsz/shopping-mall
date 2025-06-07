package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.mapper.OrderMapper;
import cn.edu.usts.cs2022.pojo.dto.OrderDTO;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.query.OrderClientQuery;
import cn.edu.usts.cs2022.pojo.vo.OrderVO;
import cn.edu.usts.cs2022.service.OrderService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    /**
     * 查询所有订单
     */
    @GetMapping("/all")
    public Result<List<Order>> selectAllOrder() {
        List<Order> list = orderService.selectAllOrder();
        return Result.success(list);
    }

    //新建订单处理
    @PostMapping("/add")
    public Result<Order> add(@RequestBody OrderDTO orderDTO) {
        System.out.println(orderDTO.toString());
        Order order =  orderService.add(orderDTO);
        return Result.success(order);
    }

    //取消订单处理
    @PostMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) {
        orderService.cancel(id);
        return Result.success();
    }

    // 订单支付处理
    @PostMapping("/pay/{id}")
    public Result pay(@PathVariable Integer id) {
        orderService.pay(id);
        return Result.success();
    }

    //客户端查询订单信息
    @PostMapping("/client/list")
    public Result<List<Order>> selectClientList(@RequestBody OrderClientQuery orderClientQuery) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        orderClientQuery.setUserId(userId);
        List<Order> orders = orderMapper.selectClientOrderList(orderClientQuery);
        return Result.success(orders);
    }
}
