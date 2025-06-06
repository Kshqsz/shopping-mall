package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.vo.client.ProductClientSimpleVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    void favorite(@Param("userId") Integer userId,
                   @Param("productId") Integer productId);

    void cancelFavorite(@Param("userId") Integer userId,
                         @Param("ids") List<Integer> ids);

    //获取当前用户收藏商品id集合
    @Select("select product_id from favorite where user_id = #{userId}")
    List<Integer> getUserFavoruteGoodIds(Integer userId);


    List<ProductClientSimpleVo> getUserFavoriteGoods(@Param("ids") List<Integer> ids);

    @Select("select id from favorite where product_id = #{id} and user_id = #{userId}")
    Integer checkStatus(@Param("id") Integer id, @Param("userId") Integer userId);
}
