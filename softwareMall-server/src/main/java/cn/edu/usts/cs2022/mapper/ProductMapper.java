package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.po.Product;
import org.apache.ibatis.annotations.*;

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

    @Insert("insert into product (name,merchant_id,first_category_id,first_category_name,second_category_id,second_category_name," +
            "video,image,description,create_time,update_time,low_price) " +
            "values (#{name},#{merchantId},#{level1Category},#{level1CategoryName},#{level2Category},#{level2CategoryName}," +
            "#{video},#{mainImage},#{description},NOW(),NOW(),#{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertProduct(ProductDTO productDTO);
}
