package cn.edu.usts.cs2022.pojo.query;

import lombok.Data;

@Data
public class OrderClientQuery {
    private Integer userId;
    private String keyword;
    private Integer status;
}
