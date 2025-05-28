package cn.edu.usts.cs2022.service.impl;

import cn.edu.usts.cs2022.mapper.FavouriteMapper;
import cn.edu.usts.cs2022.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteMapper favouriteMapper;
    @Override
    public void favourite(Integer userId, Integer productId) {
        favouriteMapper.favourite(userId, productId);
    }

    @Override
    public void cancelFavourite(Integer userId, Integer productId) {
        favouriteMapper.cancelFavourite(userId, productId);
    }
}
