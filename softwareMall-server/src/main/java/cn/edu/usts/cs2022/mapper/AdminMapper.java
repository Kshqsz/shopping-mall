package cn.edu.usts.cs2022.mapper;


import cn.edu.usts.cs2022.pojo.po.Admin;
import cn.edu.usts.cs2022.pojo.po.Category;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username = #{username} and password = #{password}")
    Admin login(@Param("username") String username,
                @Param("password") String password);



    /**
     * 查询所有商家
     * @return
     */
    @Select("select * from merchant")
    List<Merchant> selectMerchant();


}
