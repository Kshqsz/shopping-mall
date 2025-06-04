package cn.edu.usts.cs2022.pojo.vo;

import cn.edu.usts.cs2022.pojo.dto.ProductDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetailVo {
    private Long id;
    private String name;
    private Integer level1Category;
    private String level1CategoryName;
    private Integer level2Category;
    private String level2CategoryName;
    private String mainImage;
    private String description;
    private String video;
    private BigDecimal lowPrice;
    private Integer totalSales;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<SpecItemVo> specItemVos;

}
