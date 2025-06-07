package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.query.OrderClientQuery;
import cn.edu.usts.cs2022.pojo.vo.OrderVO;
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
    @Update("update orders set status = 5 ,cancel_time = NOW() where id =  #{id}")
    void cancel(@Param("id") Integer id);

    @Select("select * from orders where id = #{id}")
    Order getById(Integer id);

    @Update("update orders set status = 1 ,payment_time = NOW() where id = #{id}")
    void pay(Integer id);




    List<Order> selectClientOrderList(OrderClientQuery orderClientQuery);
}