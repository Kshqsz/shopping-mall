package cn.edu.usts.cs2022.service;

import cn.edu.usts.cs2022.pojo.vo.client.ProductClientSimpleVo;

import java.util.List;

public interface FavoriteService {
    void favorite(Integer userId, Integer productId);

    void cancelFavourite(Integer userId, List<Integer> productId);

    List<ProductClientSimpleVo> getFavorites();

}
