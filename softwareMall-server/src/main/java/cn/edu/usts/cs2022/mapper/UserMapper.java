package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.dto.CountOrderDTO;
import cn.edu.usts.cs2022.pojo.dto.UserUpdateDTO;
import cn.edu.usts.cs2022.pojo.po.Address;
import cn.edu.usts.cs2022.pojo.po.Favourite;
import cn.edu.usts.cs2022.pojo.po.User;
import cn.edu.usts.cs2022.pojo.vo.client.AddressVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username} and password = #{password}")
    User login(@Param("username") String username,
               @Param("password") String password);

    @Select("select * from user where username = #{username}")
    User getUserByUserName(String username);

    void register(User user);

    /**
     * 获取全部用户信息
     * @return
     */
    @Select("select * from user")
    List<User> selectAllUsers();

    /**
     * 修改用户状态
     * @param user
     */
    @Update("UPDATE user set status = #{status} where id = #{id}")
    void changeStatus(User user);

    @Update("update user set password = #{newPassword} where id = #{userId}")
    void updatePassword(@Param("userId") Integer userId,
                        @Param("newPassword") String newPassword);

    void update(UserUpdateDTO userUpdateDTO);

    @Select("select * from user where id = #{id}")
    User getById(Integer id);

    //添加地址
    @Insert("insert into user_address (user_id,receiver,phone,province,city,district,detail,created_at) values " +
            "(#{userId},#{name},#{phone},#{province},#{city},#{district},#{detail},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addAddress(Address address);

    //取消默认
    @Update("update user_address set is_default = false where user_id = #{userId} and is_default = true")
    void unDefault(Address address);
    // 设为默认
    @Update("update user_address set is_default = true where id = #{id}")
    void setDefault(Address address);

    //查询地址
    @Select("SELECT id, receiver as name,phone," +
            "province,city,district," +
            " detail,is_default FROM user_address WHERE user_id = #{userId};")
    List<AddressVo> addressList(Integer userId);
    //删除地址
    @Delete("delete from user_address where id = #{id}")
    void deleteAddress(Integer id);

    //修改地址
    @Update("update user_address set receiver=#{name},phone=#{phone},province=#{province},city=#{city}," +
            "district=#{district},detail=#{detail} where id = #{id}")
    void updateAddress(Address address);
    //根据id查询地址
    //查询地址
    @Select("SELECT id, receiver as name,phone," +
            "province,city,district," +
            " detail FROM user_address WHERE id = #{id};")
    AddressVo selectAddressById(Integer id);
}
