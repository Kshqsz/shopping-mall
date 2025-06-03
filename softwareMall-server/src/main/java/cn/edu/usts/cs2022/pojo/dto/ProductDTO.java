package cn.edu.usts.cs2022.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
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
    private Integer merchantId;
    private List<ProductSpecDTO> specs;
    private List<ProductSkuDTO> skus;

    @Data
    public static class ProductSpecDTO {
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
    public static class ProductSkuDTO {
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
        return "ProductDTO{" +
                "name='" + name + '\'' +
                ", level1Category=" + level1Category +
                ", level2Category=" + level2Category +
                ", mainImage='" + mainImage + '\'' +
                ", description='" + description + '\'' +
                ", video='" + video + '\'' +
                ", specs=" + specs +
                ", skus=" + skus +
                '}';
    }
}