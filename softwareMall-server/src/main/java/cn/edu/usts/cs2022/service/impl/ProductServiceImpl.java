package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.CategoryMapper;
import cn.edu.usts.cs2022.mapper.ProductMapper;
import cn.edu.usts.cs2022.mapper.SpecMapper;
import cn.edu.usts.cs2022.mapper.UserMapper;
import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.po.*;
import cn.edu.usts.cs2022.service.ProductService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService  {

    private final ProductMapper productMapper;

    private final SpecMapper specMapper;

    private final CategoryMapper categoryMapper;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ProductDTO productDTO) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer merchantId = (Integer) map.get("merchantId");
         productDTO.setMerchantId(merchantId);
         productDTO.setLevel1CategoryName(categoryMapper.selectById(productDTO.getLevel1Category()));
         productDTO.setLevel2CategoryName(categoryMapper.selectById(productDTO.getLevel2Category()));
         //插入商品表
        productMapper.insertProduct(productDTO);
        // 2. 保存规格名和规格值，并建立映射关系
        Map<String, Long> specNameMap = new HashMap<>();
        Map<Pair<String, String>, Long> specValueMap = new HashMap<>();
        for (ProductDTO.ProductSpecDTO spec : productDTO.getSpecs()) {
            // 2.1 插入规格名
            SpecName specName = new SpecName();
            specName.setName(spec.getName());
            specName.setGoodsId(productDTO.getId());
            specMapper.insertSpecName(specName);
            Long specNameId = specName.getId();
            specNameMap.put(spec.getName(), specNameId);

            // 2.2 插入规格值
            for (String value : spec.getValues()) {
                SpecValue specValue = new SpecValue();
                specValue.setSpecNameId(specNameId);
                specValue.setValue(value);
                specMapper.insertSpecValue(specValue);

                // 保存规格值ID映射：规格名+值 -> ID
                specValueMap.put(new Pair<>(spec.getName(), value), specValue.getId());
            }
        }

        // 3. 保存规格项和规格项值关联
        for (ProductDTO.ProductSkuDTO sku : productDTO.getSkus()) {
            // 3.1 插入规格项
            SpecItem specItem = new SpecItem();
            specItem.setGoodsId(productDTO.getId());
            specItem.setPrice(sku.getPrice());
            specItem.setStock(sku.getStock());
            specItem.setImage(sku.getImage());
            specItem.setName(String.join("/", sku.getSpecValues())); // 规格组合名称
            specMapper.insertSpecItem(specItem);
            Long specItemId = specItem.getId();

            // 3.2 插入规格项值关联
            for (int i = 0; i < sku.getSpecValues().size(); i++) {
                // 获取当前规格名（根据位置）
                String currentSpecName = productDTO.getSpecs().get(i).getName();
                String currentValue = sku.getSpecValues().get(i);

                // 获取规格值ID
                Long specValueId = specValueMap.get(new Pair<>(currentSpecName, currentValue));

                // 插入关联表
                SpecItemValue itemValue = new SpecItemValue();
                itemValue.setSpecItemId(specItemId);
                itemValue.setSpecValueId(specValueId);
                specMapper.insertSpecItemValue(itemValue);
            }
        }

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
