package cn.edu.usts.cs2022.mapper;

import cn.edu.usts.cs2022.pojo.dto.CountOrderDTO;
import cn.edu.usts.cs2022.pojo.dto.UserUpdateDTO;
import cn.edu.usts.cs2022.pojo.po.Favourite;
import cn.edu.usts.cs2022.pojo.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Select("select * from favourite where user_id = #{userId}")
    List<Favourite> getMyFavourite(Integer userId);

    @Select("select count(*) from favourite where user_id = #{userId}")
    Integer countFavourite(Integer userId);

    @Update("update user set password = #{newPassword} where id = #{userId}")
    void updatePassword(@Param("userId") Integer userId,
                        @Param("newPassword") String newPassword);

    void update(UserUpdateDTO userUpdateDTO);

    @Select("select * from user where id = #{id}")
    User getById(Integer id);

    Integer countOrder(CountOrderDTO countOrderDTO);
}
