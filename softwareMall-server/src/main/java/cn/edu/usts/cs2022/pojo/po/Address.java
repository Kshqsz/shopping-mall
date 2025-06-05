package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

@Data
public class Address {
    private Integer id;
    private Integer userId;  // 关联的用户ID
    private String name;  // 收货人姓名
    private String phone;  // 联系电话
    private String province;  // 省
    private String city;  // 市
    private String district;  // 区/县
    private String detail;  // 详细地址
    private Boolean isDefault = false;  // 是否默认地址

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", detail='" + detail + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
