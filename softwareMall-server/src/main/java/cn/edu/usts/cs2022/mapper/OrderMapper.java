package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.po.Order;
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


    Order getById(Integer id);

    @Update("update `order` set status = -1 where order_number =  #{orderNumber}")
    void cancel(@Param("orderNumber") String orderNumber);

    @Select("select * from `order` where order_number = #{orderNumber}")
    Order getByNumber(@Param("orderNumber") String orderNumber);

    @Update("update `order` set status = 1 where order_number = #{orderNumber}")
    void pay(String orderNumber);

    List<OrderVO> selectOrderWithPrice();
}