package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.mapper.UserMapper;
import cn.edu.usts.cs2022.pojo.dto.*;
import cn.edu.usts.cs2022.pojo.po.Favourite;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.po.User;
import cn.edu.usts.cs2022.pojo.vo.client.AddressVo;
import cn.edu.usts.cs2022.service.UserService;
import cn.edu.usts.cs2022.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public Result Login(@RequestBody LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        } else {
            Integer status = user.getStatus();
            if (status == 0) {
                return Result.error("用户已被禁用！请联系管理员");
            }
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            String token = JwtUtil.genToken(claims);
            UserDTO userDTO = new UserDTO();
            userDTO.setUser(user);
            userDTO.setUserId(user.getId());
            userDTO.setToken(token);
            return Result.success(userDTO);
        }
    }

    @PostMapping("/register")
    public Result Register(@RequestBody RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String rePassword = registerDTO.getRePassword();

        if (!password.equals(rePassword)) {
            return Result.error("两次密码输入不一致!");
        }
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return Result.error("用户名已存在");
        }
        userService.register(username, password);
        return Result.success();
    }

    /**
     * 查询所有用户
     *get
     */
    @GetMapping()
    public Result<List<User>> getUserList() {
        System.out.println("查询全部信息");
        List<User> users = userService.getUserList();
        for (User user : users) {
            user.setPassword("*********");
        }
        return Result.success(users);
    }

    /**
     * 修改用户状态
     */

    @PutMapping()
    public Result changeUserStatus(@RequestBody User user) {
        userService.changeUserStatus(user);
        return Result.success("修改成功");
    }

    @GetMapping("/myFavourite/{id}")
    public Result<List<Favourite>> getMyFavourite(@PathVariable("id") Integer userId) {
        List<Favourite> favourites = userService.getMyFavourite(userId);
        return Result.success(favourites);
    }

    @PostMapping("/countOrder")
    public Result<Integer> countOrder(@RequestBody CountOrderDTO countOrderDTO) {
        Integer count = userService.countOrder(countOrderDTO);
        return Result.success(count);
    }
    @GetMapping("/countFavourite/{id}")
    public Result<Integer> countFavourite(@PathVariable("id") Integer userId) {
        Integer count = userService.countFavourite(userId);
        return Result.success(count);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        String newPassword = updatePasswordDTO.getPassword();
        String reNewPassword = updatePasswordDTO.getRePassword();
        if (!newPassword.equals(reNewPassword)) {
            return Result.error("两次密码不一致!");
        }
        userService.updatePassword(newPassword);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.update(userUpdateDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable("id") Integer id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    // 新增地址
    @PostMapping("/addAdderss")
    public Result addAddress(@RequestBody AddressDto addressDto){
        userService.addAddress(addressDto);
        return Result.success();
    }
    // 获取所有地址
    @GetMapping("addressList")
    public Result<List<AddressVo>> getAddressList(){
        return Result.success(userService.addressList());
    }

    //删除地址
    @DeleteMapping("/address")
    public Result deleteAddress(@RequestParam Integer id){
        System.out.println(id);
        userMapper.deleteAddress(id);
        return Result.success();
    }
    //设置默认
    @PostMapping("/address")
    public Result toDefault(@RequestParam Integer id){
        userService.toDefault(id);
        return Result.success();
    }
    // 地址修改
    @PostMapping("/updateAddress")
    public Result updateAddress(@RequestBody AddressDto addressDto){
        System.out.println(addressDto.toString());
        userService.updateAddress(addressDto);
        return Result.success();
    }
}
