package cn.edu.usts.cs2022.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favourite {
    private Integer id;

    private Integer userId;

    private Integer productId;
}
