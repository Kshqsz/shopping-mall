package cn.edu.usts.cs2022.service;

import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.dto.StatusDto;
import cn.edu.usts.cs2022.pojo.po.PageResult;
import cn.edu.usts.cs2022.pojo.po.Product;
import cn.edu.usts.cs2022.pojo.query.ProductClientQuery;
import cn.edu.usts.cs2022.pojo.query.ProductSimpleQuery;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientDetailVo;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientSimpleVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductDetailVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductEditDetailVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductSimpleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductService {
    void add(ProductDTO productDTO);

    List<ProductClientSimpleVo> getAllProduct(ProductClientQuery productClientQuery);

    List<Product> getAllByMerchantId(Integer merchantId);


    List<Product> search(String searchInfo);

    ProductClientDetailVo getById(Integer id);

    List<Product> getByIds(@Param("ids") List<Integer> ids);

    /**
     * 修改商品状态
     * @param product
     */
    void updateStatus(StatusDto product);

    PageResult<ProductSimpleVo> selectSimpleProductList(ProductSimpleQuery productSimpleQuery);

    ProductDetailVo selectDetailProductById(Integer id);

    ProductEditDetailVo selectEditDetailProduct(Integer productId);

    void updateProduct(ProductDTO productDTO);
}
