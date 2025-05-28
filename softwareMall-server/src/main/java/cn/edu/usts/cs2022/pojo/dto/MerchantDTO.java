package cn.edu.usts.cs2022.pojo.dto;

import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDTO {
    private Integer merchantId;

    private Merchant merchant;

    private String token;
}
