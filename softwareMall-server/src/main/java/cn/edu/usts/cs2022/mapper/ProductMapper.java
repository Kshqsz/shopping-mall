package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.dto.StatusDto;
import cn.edu.usts.cs2022.pojo.po.Product;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.query.ProductSimpleQuery;
import cn.edu.usts.cs2022.pojo.vo.ProductDetailVo;
import cn.edu.usts.cs2022.pojo.vo.ProductEditDetailVo;
import cn.edu.usts.cs2022.pojo.vo.ProductSimpleVo;
import cn.edu.usts.cs2022.pojo.vo.ProductStatisticsVo;
import com.github.pagehelper.Page;
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

    //修改商品状态
    @Update("update product set status = #{status} where id = #{id}")
    void updateStatus(StatusDto status);

    @Insert("insert into product (name,merchant_id,first_category_id,first_category_name,second_category_id,second_category_name," +
            "video,image,description,create_time,update_time,low_price) " +
            "values (#{name},#{merchantId},#{level1Category},#{level1CategoryName},#{level2Category},#{level2CategoryName}," +
            "#{video},#{mainImage},#{description},NOW(),NOW(),#{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertProduct(ProductDTO productDTO);

    // 商家查询商品列表
    Page<ProductSimpleVo> selectSimpleProductList(ProductSimpleQuery productSimpleQuery);

    //查询单个商品基础信息
    @Select("select id,name," +
            "first_category_id as level1Category," +
            "first_category_name as level1CategoryName," +
            "second_category_id as level2Category," +
            "second_category_name as level2CategoryName," +
            "video,image as mainImage," +
            "status,description,create_time,update_time,total_sales,low_price FROM product where id = #{id}")
    ProductDetailVo selectSimpleProductById(Integer id);

    //查询待修改商品基础信息
    @Select("select id,name," +
            "first_category_id as level1Category," +
            "first_category_name as level1CategoryName," +
            "second_category_id as level2Category," +
            "second_category_name as level2CategoryName," +
            "video,image as mainImage," +
            "description,low_price AS price FROM product where id = #{productId}")
    ProductEditDetailVo selectEditProduct(Integer productId);

    // 修改商品基础信息
    @Update("update product set name = #{name},first_category_id = #{level1Category},first_category_name = #{level1CategoryName},second_category_id=#{level2Category},second_category_name=#{level2CategoryName}," +
            "video = #{video},image = #{mainImage},description= #{description},update_time = NOW(),low_price = #{price} where id = #{id}")
    void updateProduct(ProductDTO productDTO);

    @Select("SELECT " +
            "COUNT(*) AS total," +
            "COUNT(CASE WHEN status = 1 THEN 1 ELSE NULL END) AS onSale," +
            "COUNT(CASE WHEN status = 0 THEN 1 ELSE NULL END) AS pending " +
            "FROM product WHERE merchant_id = #{merchantId}")
    ProductStatisticsVo selectProductStatis(Integer merchantId);
}
