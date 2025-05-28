package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.pojo.dto.LoginDTO;
import cn.edu.usts.cs2022.pojo.po.*;
import cn.edu.usts.cs2022.service.AdminService;
import cn.edu.usts.cs2022.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {


    public final AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        Admin admin = adminService.login(username, password);

        if (admin == null) {
            return Result.error("用户名或密码错误");
        } else {
            Map<String, Object> claims = new HashMap<>();
            String token = JwtUtil.genToken(claims);
            System.out.println("登录成功"+token);
            return Result.success(token);
        }
    }

    /**
     * 查询所有商家
     */
    @GetMapping("/merchant")
    public Result<List<Merchant>> getAllMerchant() {
        List<Merchant> list = adminService.getAllMerchant();
        for (Merchant merchant : list) {
            merchant.setPassword("*********");
        }
        return Result.success(list);
    }


}
