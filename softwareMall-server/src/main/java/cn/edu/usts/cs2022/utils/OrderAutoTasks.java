package cn.edu.usts.cs2022.utils;

import cn.edu.usts.cs2022.mapper.OrderMapper;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderAutoTasks {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 自动取消未支付订单（每分钟执行一次）
     */
    @Scheduled(fixedRate = 300000) // 每分钟执行一次
    public void autoCancelUnpaidOrders() {
        log.info("开始执行自动取消未支付订单任务...");
        try {
            List<Order> orders = orderMapper.selectAutoCancelOrders();
            for (Order order : orders) {
                orderService.cancel(order.getId());
            }
        } catch (Exception e) {
            log.error("自动取消未支付订单任务失败", e);
        }
    }

    /**
     * 自动确认收货（每天凌晨2点执行）
     */
    @Scheduled(cron = "0 0 23 * * ?") // 每天凌晨2点执行
    public void autoConfirmDeliveredOrders() {
        log.info("开始执行自动确认收货任务...");
        try {
            List<Order> orders = orderMapper.selectAutoConfirmOrders();
            for (Order order : orders) {
                orderService.receive(order.getId());
            }
        } catch (Exception e) {
            log.error("自动确认收货任务失败", e);
        }
    }

    /**
     * 自动处理退货退款（每30分钟执行一次）
     */
    @Scheduled(fixedRate = 1800000) // 每30分钟执行一次
    public void autoProcessRefunds() {
        log.info("开始执行自动处理退货退款任务...");
        try {
            List<Order> orders = orderMapper.selectAutoRefundOrders();
            for (Order order : orders) {
                orderService.agreeReturn(order.getId());
            }
        } catch (Exception e) {
            log.error("自动处理退货退款任务失败", e);
        }
    }
}
