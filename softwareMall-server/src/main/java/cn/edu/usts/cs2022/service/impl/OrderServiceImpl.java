package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.OrderMapper;

import cn.edu.usts.cs2022.pojo.dto.OrderDTO;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.vo.OrderVO;
import cn.edu.usts.cs2022.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    /**
     * 查询所有订单信息
     * @return
     */
    @Override
    public List<Order> selectAllOrder() {
        return orderMapper.selectAllOrder();
    }

    @Override
    public Order add(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        String orderNumber = String.valueOf(System.currentTimeMillis());
        order.setOrderNumber(orderNumber);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        orderMapper.add(order);

        System.out.println(order.getId());
        return order;
    }

    @Override
    public void cancel(String orderNumber) {
        orderMapper.cancel(orderNumber);
    }

    @Override
    public void pay(String orderNumber) {
        orderMapper.pay(orderNumber);
    }

    @Override
    public List<OrderVO> selectOrderWithPrice() {
        return orderMapper.selectOrderWithPrice();
    }
}
