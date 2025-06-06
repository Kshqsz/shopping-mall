package cn.edu.usts.cs2022.controller;


import cn.edu.usts.cs2022.mapper.ProductMapper;
import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.dto.StatusDto;
import cn.edu.usts.cs2022.pojo.po.PageResult;
import cn.edu.usts.cs2022.pojo.po.Product;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.query.ProductClientQuery;
import cn.edu.usts.cs2022.pojo.query.ProductSimpleQuery;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientDetailVo;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientSimpleVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductDetailVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductEditDetailVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductSimpleVo;
import cn.edu.usts.cs2022.pojo.vo.merchant.ProductStatisticsVo;
import cn.edu.usts.cs2022.service.ProductService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;
    //新增商品
    @PostMapping("/add")
    public Result add(@RequestBody ProductDTO productDTO) {
        System.out.println(productDTO.toString());
        productService.add(productDTO);
        return Result.success();
    }

    //根据条件获取商品信息
    @PostMapping("/all")
    public Result<List<ProductClientSimpleVo>> getAllProduct(@RequestBody ProductClientQuery productClientQuery) {
        System.out.println(productClientQuery.toString());
        List<ProductClientSimpleVo> productList = productService.getAllProduct(productClientQuery);
        return Result.success(productList);
    }

    @GetMapping("/all/{merchantId}")
    public Result<List<Product>> getAllByMerchantId(@PathVariable("merchantId") Integer merchantId) {
        List<Product> productList = productService.getAllByMerchantId(merchantId);
        return Result.success(productList);
    }


    @GetMapping("/search")
    public Result<List<Product>> search(String searchInfo) {
        List<Product> productList = productService.search(searchInfo);
        return Result.success(productList);
    }
    // 客户端获取单个详细商品信息
    @GetMapping("/{id}")
    public Result<ProductClientDetailVo> getById(@PathVariable("id") Integer id) {
        ProductClientDetailVo product = productService.getById(id);
        return Result.success(product);
    }

    @PostMapping("/getByIds")
    public Result<List<Product>> getByIds(@RequestBody List<Integer> ids) {
        if (ids.size() == 0) {
            return Result.success(List.of());
        }
        List<Product> products = productService.getByIds(ids);
        return Result.success(products);
    }

    /**
     * 修改商品状态
     * @param
     * @return
     */
    @PostMapping("/status")
    public Result updateStatus(@RequestBody StatusDto statusDto) {
        productService.updateStatus(statusDto);
        return Result.success();
    }

    //根据条件查询商品简略信息
    @PostMapping("/fetch")
    public Result selectSimpleProductList(@RequestBody ProductSimpleQuery productSimpleQuery) {
        PageResult<ProductSimpleVo> productSimpleVoPageResult = productService.selectSimpleProductList(productSimpleQuery);
        return Result.success(productSimpleVoPageResult);
    }

    // 查询商品详细信息
    @GetMapping("/detail/{id}")
    public Result<ProductDetailVo> selectDetailProductById(@PathVariable Integer id) {
        ProductDetailVo productDetailVo = productService.selectDetailProductById(id);
        return Result.success(productDetailVo);
    }

    // 查询待修改商品信息
    @GetMapping("/editDetail/{id}")
    public Result<ProductEditDetailVo> selectEditDetailProduct(@PathVariable Integer id) {
        System.out.println(1);
        return Result.success(productService.selectEditDetailProduct(id));
    }

    //修改商品信息
    @PostMapping("/update")
    public Result updateProduct(@RequestBody ProductDTO productDTO) {
        productService.updateProduct(productDTO);
        return Result.success();
    }

    //获取商品统计信息
    @GetMapping("/statis")
    public Result<ProductStatisticsVo> selectProductStatistics() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer merchantId = (Integer) map.get("merchantId");
        return Result.success(productMapper.selectProductStatis(merchantId));
    }
}
