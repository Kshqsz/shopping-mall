package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.*;

import cn.edu.usts.cs2022.pojo.dto.DeliverProductDto;
import cn.edu.usts.cs2022.pojo.dto.OrderDTO;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.po.Order;
import cn.edu.usts.cs2022.pojo.po.SpecItem;
import cn.edu.usts.cs2022.pojo.po.User;
import cn.edu.usts.cs2022.pojo.vo.client.AddressVo;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientDetailVo;
import cn.edu.usts.cs2022.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final SpecMapper specMapper;
    private final AdminMapper adminMapper;

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
        Integer userId = orderDTO.getUserId();
        Integer productId = orderDTO.getProductId();
        Integer specId = orderDTO.getSpecId();
        Integer addressId = orderDTO.getAddressId();
        Integer merchantId = orderDTO.getMerchantId();
        Order order = new Order();
        //基本信息
        order.setOrderNo(orderDTO.getOrderNo());
        order.setProductPrice(orderDTO.getProductPrice());
        order.setQuantity(orderDTO.getQuantity());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setStatus(0);
        //用户相关信息
        User user = userMapper.getById(userId);
        order.setUserId(userId);
        order.setUsername(user.getUsername());
        //商品相关信息
        ProductClientDetailVo product = productMapper.getProductClientDetailById(productId);
        order.setProductId(productId);
        order.setProductName(product.getName());
        //规格相关信息
        SpecItem specItem = specMapper.selectSpecItemsById(specId);
        order.setSpecId(specId);
        order.setProductSpecs(specItem.getName());
        order.setProductImage(specItem.getImage());
        //地址相关信息
        AddressVo addressVo = userMapper.selectAddressById(addressId);
        order.setReceiverName(addressVo.getName());
        order.setReceiverPhone(addressVo.getPhone());
        order.setReceiverProvince(addressVo.getProvince());
        order.setReceiverCity(addressVo.getCity());
        order.setReceiverDistrict(addressVo.getDistrict());
        order.setReceiverDetail(addressVo.getDetail());
        //商户相关信息
        Merchant merchant = adminMapper.selectMerchantById(merchantId);
        order.setMerchantId(merchantId);
        order.setShopName(merchant.getShopName());
        order.setCreateTime(LocalDateTime.now());
        //自动取消时间
        LocalDateTime thirtyMinutesLater = LocalDateTime.now().plusMinutes(5);
        order.setAutoCancelTime(thirtyMinutesLater);
        orderMapper.add(order);
        return order;
    }

    @Override
    public void cancel(Integer id) {
        orderMapper.cancel(id);
    }

    //处理支付订单
    @Override
    public void pay(Integer id) {
        // 判断库存是否足够 查询库存信息
        Order order = orderMapper.getById(id);
        Integer specId = order.getSpecId();
        Integer quantity = order.getQuantity();
        Integer stock = specMapper.getStock(specId);
        if (stock < quantity) {
            throw new RuntimeException("库存不足，无法购买");
        }
        // 修改库存
        Integer newStock = stock - quantity;
        specMapper.updateStock(specId,newStock);



        orderMapper.pay(id);
    }

    // 处理发货
    @Override
    public void delever(DeliverProductDto deliverProductDto) {
        orderMapper.deliver(deliverProductDto);
    }

    @Transactional
    @Override
    public void receive(Integer id) {
        //更新订单状态
        orderMapper.receive(id);
        Order order = orderMapper.getById(id);
        Integer specId = order.getSpecId();
        Integer quantity = order.getQuantity();
        Integer productId = order.getProductId();
        //更新规格销量明细
        specMapper.updateSales(specId,quantity);
        //更新商品总销量
        productMapper.updateSales(productId,quantity);
    }

    @Transactional
    @Override
    public void toReturnStatus(Integer id) {
        //若已支付但未发货，则直接退款
        Order order = orderMapper.getById(id);
        if (order.getStatus() == 1) {
            System.out.println("直接退款");
            //库存复原
            Integer quantity = order.getQuantity();
            Integer specId = order.getSpecId();
            Integer stock = specMapper.getStock(specId);
            specMapper.updateStock(specId,quantity+stock);
            //订单状态为已退款
            orderMapper.toReturnStatus(id,7);
            System.out.println(id);
            System.out.println("退款成功");
        }
        else {
            System.out.println("商家同意后退款");
            //若已经发货，则需要商家收到货物同意才能退款等待商家处理
            orderMapper.toReturnStatus(id,6);
        }


    }

    //商家同意退款
    @Override
    public void agreeReturn(Integer id) {
        Order order = orderMapper.getById(id);
        //库存复原
        Integer quantity = order.getQuantity();
        Integer specId = order.getSpecId();
        Integer stock = specMapper.getStock(specId);
        specMapper.updateStock(specId,quantity+stock);
        //订单状态为已退款
        orderMapper.toReturnStatus(id,7);
    }




}
