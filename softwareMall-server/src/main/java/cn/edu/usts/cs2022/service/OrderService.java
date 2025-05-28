package cn.edu.usts.cs2022.service;

import cn.edu.usts.cs2022.pojo.dto.OrderDTO;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 查询所有订单信息
     * @return
     */
    List<Order> selectAllOrder();

    Order add(OrderDTO orderDTO);

    void cancel(String orderNumber);

    void pay(String orderNumber);

    /**
     * 获取带有价格的订单
     * @return
     */
    List<OrderVO> selectOrderWithPrice();
}
