package cn.edu.usts.cs2022.mapper;

import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavouriteMapper {

    void favourite(@Param("userId") Integer userId,
                   @Param("productId") Integer productId);

    @Delete("delete from favourite where user_id = #{userId} and product_id = #{productId}")
    void cancelFavourite(@Param("userId") Integer userId,
                         @Param("productId")Integer productId);
}
