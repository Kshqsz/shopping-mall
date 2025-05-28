package cn.edu.usts.cs2022.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String name;

    private BigDecimal price;

    private Integer categoryId;

    private String video;

    private String image;

    private String source;

    private String description;
}
