package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.dto.DeliverProductDto;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.query.OrderClientQuery;
import cn.edu.usts.cs2022.pojo.query.OrderMerchantQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 查询所有订单信息
     *
     * @return
     */
    @Select("SELECT * from `order` ")
    List<Order> selectAllOrder();

    int add(Order order);



    //取消订单
    @Update("update orders set status = 5 ,cancel_time = NOW() where id =  #{id} and status = 0")
    void cancel(@Param("id") Integer id);

    @Select("select * from orders where id = #{id}")
    Order getById(Integer id);

    @Update("update orders set status = 1 ,payment_time = NOW() where id = #{id} and status = 0")
    void pay(Integer id);




    List<Order> selectClientOrderList(OrderClientQuery orderClientQuery);

    @Select("select * from orders where id = #{id}")
    Order selectOrderById(Integer id);

    List<Order> selectMerchantOrderList(OrderMerchantQuery searchQuery);

    //处理发货
    @Update("update orders set shipping_company = #{shippingCompany},shipping_number = #{shippingNumber}" +
            ",status = 2,shipping_time=NOW(), auto_confirm_time = (NOW() + INTERVAL 10 DAY)  where id = #{id} and status = 1")
    void deliver(DeliverProductDto deliverProductDto);

    @Update("update orders set complete_time = NOW(),status=4 where id = #{id} and status = 2")
    void receive(Integer id);

    @Update("update orders set status = #{status},refund_time = NOW() ,auto_refund_time = (NOW() + INTERVAL 5 DAY) where id = #{id}")
    void toReturnStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Select("select * from orders where product_id = #{productId} and status in (0,1,2,6)")
    List<Order> getOrderByProductId(Integer productId);

    //查询过期未支付订单
    @Select("select id from orders where status = 0 and auto_cancel_time < NOW()")
    List<Order> selectAutoCancelOrders();

    //查询超时未确认付款订单
    @Select("select id  from orders where status = 2 and auto_confirm_time < NOW()")
    List<Order> selectAutoConfirmOrders();

    //查询超时未处理退款订单
    @Select("select id from orders  where status = 6 and  auto_refund_time < NOW()")
    List<Order> selectAutoRefundOrders();
}