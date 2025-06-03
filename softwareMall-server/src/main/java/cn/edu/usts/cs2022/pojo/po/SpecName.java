package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

import java.util.Date;

@Data
public class SpecName {
    private Long id;
    private String name;
    private Long goodsId;
    private Date createTime;
}
