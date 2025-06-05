package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.po.SpecItem;
import cn.edu.usts.cs2022.pojo.po.SpecItemValue;
import cn.edu.usts.cs2022.pojo.po.SpecName;
import cn.edu.usts.cs2022.pojo.po.SpecValue;
import cn.edu.usts.cs2022.pojo.vo.merchant.SpecItemVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpecMapper {

    @Insert("INSERT INTO spec_name (name, goods_id, create_time) " +
            "VALUES (#{name}, #{goodsId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertSpecName(SpecName specName);

    @Insert("INSERT INTO spec_value (spec_name_id, value, create_time) " +
            "VALUES (#{specNameId}, #{value}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertSpecValue(SpecValue specValue);

    @Insert("INSERT INTO spec_item (goods_id, name, price, stock, image, sales, create_time, update_time) " +
            "VALUES (#{goodsId}, #{name}, #{price}, #{stock}, #{image}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertSpecItem(SpecItem specItem);

    @Insert("INSERT INTO spec_item_value (spec_item_id, spec_value_id, create_time) " +
            "VALUES (#{specItemId}, #{specValueId}, NOW())")
    void insertSpecItemValue(SpecItemValue itemValue);


    @Select("select id,goods_id,name,price,stock,image,sales from spec_item where goods_id = #{id}")
    List<SpecItemVo> selectSpecItems(Integer id);


    // 根据商品ID查询规格名
    @Select("SELECT * FROM spec_name WHERE goods_id = #{goodsId}")
    List<SpecName> selectSpecNamesByGoodsId(Integer goodsId);


    // 根据规格名ID查询规格值
    @Select("SELECT * FROM spec_value WHERE spec_name_id = #{specNameId}")
    List<SpecValue> selectSpecValuesBySpecNameId(Long specNameId);

    @Select("SELECT * FROM spec_item WHERE goods_id = #{goodsId}")
    List<SpecItem> selectSpecItemsByGoodsId(Integer goodsId);

    // 根据规格名和值查询规格值
    @Select("SELECT * FROM spec_value WHERE value = #{value} AND spec_name_id = " +
            "(SELECT id FROM spec_name WHERE name = #{name} AND goods_id = #{goodsId})")
    SpecValue selectSpecValueByNameAndValue(@Param("name") String name,
                                            @Param("value") String value,
                                            @Param("goodsId") Integer goodsId);

    // 根据规格项ID查询规格项值关联
    @Select("SELECT * FROM spec_item_value WHERE spec_item_id = #{specItemId}")
    List<SpecItemValue> selectSpecItemValuesByItemId(Long specItemId);

    @Select("SELECT * FROM spec_value WHERE id = #{id}")
    SpecValue selectSpecValueById(@Param("id") Long id);


    @Select("SELECT * FROM spec_name WHERE id = #{id}")
    SpecName selectSpecNameById(@Param("id") Long id);


    // 删除规格项值关联
    @Delete("DELETE FROM spec_item_value WHERE spec_item_id IN " +
            "(SELECT id FROM spec_item WHERE goods_id = #{goodsId})")
    void deleteSpecItemValuesByGoodsId(@Param("goodsId") Long goodsId);

    // 删除规格项
    @Delete("DELETE FROM spec_item WHERE goods_id = #{goodsId}")
    void deleteSpecItemsByGoodsId(@Param("goodsId") Long goodsId);

    // 删除规格值
    @Delete("DELETE FROM spec_value WHERE spec_name_id IN " +
            "(SELECT id FROM spec_name WHERE goods_id = #{goodsId})")
    void deleteSpecValuesByGoodsId(@Param("goodsId") Long goodsId);

    // 删除规格名
    @Delete("DELETE FROM spec_name WHERE goods_id = #{goodsId}")
    void deleteSpecNamesByGoodsId(@Param("goodsId") Long goodsId);
}
