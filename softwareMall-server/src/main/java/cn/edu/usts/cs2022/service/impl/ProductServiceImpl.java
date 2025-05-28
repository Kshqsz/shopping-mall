package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.ProductMapper;
import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.po.Product;
import cn.edu.usts.cs2022.service.ProductService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService  {

    private final ProductMapper productMapper;

    @Override
    public void add(ProductDTO productDTO) {
        Product product = new Product();

        BeanUtils.copyProperties(productDTO, product);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer merchantId = (Integer) map.get("merchantId");
        System.out.println(merchantId);
        product.setMerchantId(merchantId);
        product.setStatus(0); // 表示待审核
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.add(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productMapper.getAllProduct();
    }

    @Override
    public List<Product> getAllByMerchantId(Integer merchantId) {
        return productMapper.getAllByMerchantId(merchantId);
    }

    @Override
    public void update(Product product) {
        productMapper.update(product);
    }

    @Override
    public List<Product> search(String searchInfo) {
        List<Product> productList = productMapper.search(searchInfo);
        return productList;
    }

    @Override
    public Product getById(Integer id) {
        return productMapper.getById(id);
    }

    @Override
    public List<Product> getByIds(List<Integer> ids) {
        return productMapper.getByIds(ids);
    }

    @Override
    public void updateStatus(Product product) {
        productMapper.updateStatus(product);
    }
}
