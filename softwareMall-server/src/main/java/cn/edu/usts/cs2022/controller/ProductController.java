package cn.edu.usts.cs2022.controller;


import cn.edu.usts.cs2022.mapper.ProductMapper;
import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.dto.StatusDto;
import cn.edu.usts.cs2022.pojo.po.PageResult;
import cn.edu.usts.cs2022.pojo.po.Product;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.query.ProductSimpleQuery;
import cn.edu.usts.cs2022.pojo.vo.ProductDetailVo;
import cn.edu.usts.cs2022.pojo.vo.ProductEditDetailVo;
import cn.edu.usts.cs2022.pojo.vo.ProductSimpleVo;
import cn.edu.usts.cs2022.pojo.vo.ProductStatisticsVo;
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
    @PostMapping("/add")
    public Result add(@RequestBody ProductDTO productDTO) {
        System.out.println(productDTO.toString());
        productService.add(productDTO);
        return Result.success();
    }

    @GetMapping("/all")
    public Result<List<Product>> getAllProduct() {
        List<Product> productList = productService.getAllProduct();
        return Result.success(productList);
    }

    @GetMapping("/all/{merchantId}")
    public Result<List<Product>> getAllByMerchantId(@PathVariable("merchantId") Integer merchantId) {
        List<Product> productList = productService.getAllByMerchantId(merchantId);
        return Result.success(productList);
    }

    @PutMapping
    public Result update(@RequestBody Product product) {
        productService.update(product);
        return Result.success();
    }

    @GetMapping("/search")
    public Result<List<Product>> search(String searchInfo) {
        List<Product> productList = productService.search(searchInfo);
        return Result.success(productList);
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable("id") Integer id) {
        Product product = productService.getById(id);
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
        System.out.println(productSimpleQuery.toString());
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

    @PostMapping("/update")
    public Result updateProduct(@RequestBody ProductDTO productDTO) {
        System.out.println(productDTO.toString());
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
