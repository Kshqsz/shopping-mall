package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.CategoryMapper;
import cn.edu.usts.cs2022.mapper.ProductMapper;
import cn.edu.usts.cs2022.mapper.SpecMapper;
import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import cn.edu.usts.cs2022.pojo.dto.StatusDto;
import cn.edu.usts.cs2022.pojo.po.*;
import cn.edu.usts.cs2022.pojo.query.ProductSimpleQuery;
import cn.edu.usts.cs2022.pojo.vo.ProductDetailVo;
import cn.edu.usts.cs2022.pojo.vo.ProductEditDetailVo;
import cn.edu.usts.cs2022.pojo.vo.ProductSimpleVo;
import cn.edu.usts.cs2022.service.ProductService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final SpecMapper specMapper;

    private final CategoryMapper categoryMapper;

    // 添加商品
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
    public void updateStatus(StatusDto statusDto) {
        productMapper.updateStatus(statusDto);
    }

    // 获取简略商品列表
    @Override
    public PageResult<ProductSimpleVo> selectSimpleProductList(ProductSimpleQuery productSimpleQuery) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer merchantId = (Integer) map.get("merchantId");
        productSimpleQuery.setMerchant_id(merchantId);
        // 1. 设置分页参数（pageNum: 页码, pageSize: 每页条数）
        PageHelper.startPage(productSimpleQuery.getPage(), productSimpleQuery.getSize());

        Page<ProductSimpleVo> page = productMapper.selectSimpleProductList(productSimpleQuery);
        // 4. 返回自定义分页结果
        PageResult pageResult = new PageResult();
        pageResult.setList(page.getResult());
        pageResult.setTotal(page.getTotal());
        return pageResult;

    }

    // 查询单个商品详细信息
    @Override
    public ProductDetailVo selectDetailProductById(Integer id) {
        ProductDetailVo productDetailVo = productMapper.selectSimpleProductById(id);
        productDetailVo.setSpecItemVos(specMapper.selectSpecItems(id));
        return productDetailVo;
    }

    //获取待修改商品的基础信息
    @Override
    public ProductEditDetailVo selectEditDetailProduct(Integer productId) {
        ProductEditDetailVo productEditDetailVo = productMapper.selectEditProduct(productId);// 2. 查询规格信息
        List<ProductEditDetailVo.ProductSpec> specs = getProductSpecs(productId);

        productEditDetailVo.setSpecs(specs);

        // 3. 查询SKU信息
        List<ProductEditDetailVo.ProductSku> skus = getProductSkus(productId, specs);
        productEditDetailVo.setSkus(skus);
        return productEditDetailVo;
    }
    private List<ProductEditDetailVo.ProductSpec> getProductSpecs(Integer productId) {
        // 查询所有规格名
        List<SpecName> specNames = specMapper.selectSpecNamesByGoodsId(productId);

        // 组装规格DTO
        List<ProductEditDetailVo.ProductSpec> specDTOs = new ArrayList<>();
        for (SpecName specName : specNames) {
            ProductEditDetailVo.ProductSpec specDTO = new ProductEditDetailVo.ProductSpec();
            specDTO.setName(specName.getName());

            // 查询该规格名下的所有规格值
            List<SpecValue> specValues = specMapper.selectSpecValuesBySpecNameId(specName.getId());
            List<String> values = specValues.stream()
                    .map(SpecValue::getValue)
                    .collect(Collectors.toList());

            specDTO.setValues(values);
            specDTOs.add(specDTO);
        }

        return specDTOs;
    }
    private List<ProductEditDetailVo.ProductSku> getProductSkus(Integer productId,
                                                                List<ProductEditDetailVo.ProductSpec> specs) {
        // 查询所有规格项
        List<SpecItem> specItems = specMapper.selectSpecItemsByGoodsId(productId);

        // 创建规格值映射：规格名+值 -> 规格值ID
        Map<String, Long> specValueIdMap = new HashMap<>();
        for (ProductEditDetailVo.ProductSpec spec : specs) {
            for (String value : spec.getValues()) {
                SpecValue specValue = specMapper.selectSpecValueByNameAndValue(spec.getName(), value, productId);
                if (specValue != null) {
                    // 使用规格名和规格值组合作为key
                    String key = spec.getName() + ":" + value;
                    specValueIdMap.put(key, specValue.getId());
                }
            }
        }

        // 组装SKU DTO
        List<ProductEditDetailVo.ProductSku> skuDTOs = new ArrayList<>();
        for (SpecItem specItem : specItems) {
            ProductEditDetailVo.ProductSku skuDTO = new ProductEditDetailVo.ProductSku();
            skuDTO.setPrice(specItem.getPrice());
            skuDTO.setStock(specItem.getStock());
            skuDTO.setImage(specItem.getImage());

            // 获取规格值组合
            List<String> specValues = new ArrayList<>();

            // 查询规格项关联的规格值
            List<SpecItemValue> itemValues = specMapper.selectSpecItemValuesByItemId(specItem.getId());

            // 按规格名顺序排列规格值
            for (SpecItemValue itemValue : itemValues) {
                SpecValue specValue = specMapper.selectSpecValueById(itemValue.getSpecValueId());
                if (specValue != null) {
                    // 找到对应的规格名
                    String specName = specMapper.selectSpecNameById(specValue.getSpecNameId()).getName();

                    // 将规格值添加到指定位置
                    for (ProductEditDetailVo.ProductSpec spec : specs) {
                        if (spec.getName().equals(specName)) {
                            specValues.add(specValue.getValue());
                            break;
                        }
                    }
                }
            }

            skuDTO.setSpecValues(specValues);
            skuDTOs.add(skuDTO);
        }

        return skuDTOs;
    }

    // 更新商品信息
    @Transactional
    @Override
    public void updateProduct(ProductDTO productDTO) {
        productDTO.setLevel1CategoryName(categoryMapper.selectById(productDTO.getLevel1Category()));
        productDTO.setLevel2CategoryName(categoryMapper.selectById(productDTO.getLevel2Category()));
        //更新基础信息
        productMapper.updateProduct(productDTO);
        // 删除原有规格信息
        // 2. 删除原有规格信息（按正确顺序）
        deleteExistingSpecs(productDTO.getId());

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
    private void deleteExistingSpecs(Long productId) {
        // 1. 删除规格项值关联
        specMapper.deleteSpecItemValuesByGoodsId(productId);

        // 2. 删除规格项
        specMapper.deleteSpecItemsByGoodsId(productId);

        // 3. 删除规格值
        specMapper.deleteSpecValuesByGoodsId(productId);

        // 4. 删除规格名
        specMapper.deleteSpecNamesByGoodsId(productId);
    }
}