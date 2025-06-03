package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

import java.util.Date;

@Data
public class SpecValue {
    private Long id;
    private Long specNameId;
    private String value;
    private Date createTime;
}
