package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

import java.util.Date;

@Data
public class SpecItemValue {
    private Long id;
    private Long specItemId;
    private Long specValueId;
    private Date createTime;
}
