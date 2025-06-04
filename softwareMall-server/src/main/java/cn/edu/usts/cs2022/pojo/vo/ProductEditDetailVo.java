package cn.edu.usts.cs2022.pojo.vo;

import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductEditDetailVo {
    private Long id;
    private String name;
    private Integer level1Category;
    private String level1CategoryName;
    private Integer level2Category;
    private String level2CategoryName;
    private String mainImage;
    private String description;
    private String video;
    private BigDecimal price;
    private List<ProductEditDetailVo.ProductSpec> specs;
    private List<ProductEditDetailVo.ProductSku> skus;

    @Data
    public static class ProductSpec {
        private String name;
        private List<String> values;

        @Override
        public String toString() {
            return "ProductSpecDTO{" +
                    "name='" + name + '\'' +
                    ", values=" + values +
                    '}';
        }
    }
    @Data
    public static class ProductSku {
        private List<String> specValues;
        private BigDecimal price;
        private Integer stock;
        private String image;

        @Override
        public String toString() {
            return "ProductSkuDTO{" +
                    "specValues=" + specValues +
                    ", price=" + price +
                    ", stock=" + stock +
                    ", image='" + image + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProductEditDetailVo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level1Category=" + level1Category +
                ", level1CategoryName='" + level1CategoryName + '\'' +
                ", level2Category=" + level2Category +
                ", level2CategoryName='" + level2CategoryName + '\'' +
                ", mainImage='" + mainImage + '\'' +
                ", description='" + description + '\'' +
                ", video='" + video + '\'' +
                ", price=" + price +
                ", specs=" + specs +
                ", skus=" + skus +
                '}';
    }
}
