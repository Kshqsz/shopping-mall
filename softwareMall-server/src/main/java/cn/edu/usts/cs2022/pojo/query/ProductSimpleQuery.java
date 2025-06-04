package cn.edu.usts.cs2022.pojo.query;

import lombok.Data;

@Data
public class ProductSimpleQuery {
    private Integer merchant_id;
    private String name;
    private Integer level1Category;
    private Integer level2Category;
    private Integer status;
    private Integer page;
    private Integer size;

    @Override
    public String toString() {
        return "ProductSimpleQuery{" +
                "name='" + name + '\'' +
                ", level1Category=" + level1Category +
                ", level2Category=" + level2Category +
                ", status=" + status +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
