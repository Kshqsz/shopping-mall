package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.FavoriteMapper;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientSimpleVo;
import cn.edu.usts.cs2022.service.FavoriteService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    @Override
    public void favorite(Integer userId, Integer productId) {
        favoriteMapper.favorite(userId, productId);
    }

    @Override
    public void cancelFavourite(Integer userId, List<Integer> ids) {
        favoriteMapper.cancelFavorite(userId, ids);
    }

    @Override
    public List<ProductClientSimpleVo> getFavorites() {
        // 获取当前用户收藏商品ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        List<Integer> ids = favoriteMapper.getUserFavoruteGoodIds(userId);
        List<ProductClientSimpleVo> favorites = favoriteMapper.getUserFavoriteGoods(ids);

        return favorites;
    }
}
