package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    // 总记录数
    private Integer total;
    // 分页数据
    private List<T> list;
}
