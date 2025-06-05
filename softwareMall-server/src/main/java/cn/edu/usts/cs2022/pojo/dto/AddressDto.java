package cn.edu.usts.cs2022.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddressDto {
    private Integer id;
    private String name;
    private String phone;
    private List<String> region;
    private String detail;
    private Boolean isDefault;

    @Override
    public String toString() {
        return "AddressDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", region=" + region +
                ", detail='" + detail + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
