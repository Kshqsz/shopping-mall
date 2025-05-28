package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.po.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {
    void add(Product product);

    @Select("select * from product")
    List<Product> getAllProduct();

    @Select("select * from product where merchant_id = #{merchantId}")
    List<Product> getAllByMerchantId(Integer merchantId);

    void update(Product product);

    List<Product> search(String searchInfo);

    @Select("select * from product where id = #{id}")
    Product getById(@Param("id") Integer id);

    List<Product> getByIds(@Param("ids") List<Integer> ids);

    /**
     * 修改商品状态
     * @param product
     */
    @Update("update product set status = #{status} where id = #{id}")
    void updateStatus(Product product);
}
