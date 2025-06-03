package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.dto.UserUpdateDTO;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.vo.MerchantVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MerchantMapper {
    @Select("select * from merchant where username = #{username} and password = #{password}")
    Merchant login(@Param("username") String username,
                   @Param("password") String password);

    @Select("select * from merchant where username = #{username}")
    Merchant getMerchantByUsername(@Param("username") String username);

    void register(Merchant merchant);

    /**
     * 查询所有商家
     * @return
     */
    @Select("SELECT * from merchant")
    List<Merchant> selectAllMerchant();

    @Update("update merchant set status = #{status} where id = #{id}")
    void changeStatus(Merchant merchant);

    MerchantVo getByProductId(@Param("id") Integer id);

    @Select("select * from merchant where id = #{id}")
    Merchant getById(@Param("id") Integer id);

    @Update("update merchant set password = #{newPassword} where id = #{userId}")
    void updatePassword(@Param("userId") Integer userId,
                        @Param("newPassword") String newPassword);

    void update(UserUpdateDTO userUpdateDTO);
}
