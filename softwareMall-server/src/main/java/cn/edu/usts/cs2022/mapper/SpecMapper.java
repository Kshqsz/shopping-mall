package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.po.SpecItem;
import cn.edu.usts.cs2022.pojo.po.SpecItemValue;
import cn.edu.usts.cs2022.pojo.po.SpecName;
import cn.edu.usts.cs2022.pojo.po.SpecValue;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

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
}
