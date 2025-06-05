package cn.edu.usts.cs2022.pojo.vo.client;

import lombok.Data;

@Data
public class AddressVo {
    private Long id;
    private String name;
    private String phone;
    private String province;  // 省
    private String city;  // 市
    private String district;  // 区/县
    private String detail;
    private Boolean isDefault;
}
