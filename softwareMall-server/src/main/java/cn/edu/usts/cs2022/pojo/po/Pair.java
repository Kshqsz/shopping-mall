package cn.edu.usts.cs2022.pojo.po;

import lombok.Data;

@Data
public class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    
}
