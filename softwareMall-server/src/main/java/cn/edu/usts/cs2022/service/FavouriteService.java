package cn.edu.usts.cs2022.service;

public interface FavouriteService {
    void favourite(Integer userId, Integer productId);

    void cancelFavourite(Integer userId, Integer productId);
}
