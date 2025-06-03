package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.pojo.dto.*;
import cn.edu.usts.cs2022.pojo.po.Merchant;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.MerchantVo;
import cn.edu.usts.cs2022.service.MerchantService;
import cn.edu.usts.cs2022.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    /**
     * 商家登录
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        Merchant merchant = merchantService.login(username, password);
        if (merchant == null) {
            return Result.error("用户名或密码错误");
        } else {
            Integer status = merchant.getStatus();
            if (status == 0) {
                return Result.error("商家已被禁用！请联系管理员");
            }
            Map<String, Object> claims = new HashMap<>();
            claims.put("merchantId", merchant.getId());
            String token = JwtUtil.genToken(claims);
            MerchantDTO merchantDTO = new MerchantDTO();
            merchantDTO.setMerchant(merchant);
            merchantDTO.setMerchantId(merchant.getId());
            merchantDTO.setToken(token);
            return Result.success(merchantDTO);
        }
    }

    /**
     * 商家注册
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    public Result Register(@RequestBody RegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String rePassword = registerDTO.getRePassword();

        if (!password.equals(rePassword)) {
            return Result.error("两次密码输入不一致!");
        }
        Merchant merchant = merchantService.getMerchantByUsername(username);
        if (merchant != null) {
            return Result.error("用户名已存在");
        }
        merchantService.register(username, password);
        return Result.success();
    }

    /**
     * 查询所有商家
     */
    @GetMapping
    public Result<List<Merchant>> getAllMerchants() {
        List<Merchant> list = merchantService.getAllMerchant();
        return Result.success(list);
    }

    /**
     * 修改商家状态
     */
    @PutMapping("/status")
    public Result ChangeStatus(@RequestBody Merchant merchant){
        merchantService.changeStatus(merchant);
        return Result.success();
    }


    @GetMapping("/getByProductId/{id}")
    public Result<MerchantVo> getByProductId(@PathVariable("id") Integer id) {
        MerchantVo merchantVO = merchantService.getByProductId(id);
        return Result.success(merchantVO);
    }

    @GetMapping("{id}")
    public Result<Merchant> getById(@PathVariable("id") Integer id) {
        Merchant merchant = merchantService.getById(id);
        return Result.success(merchant);
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        String newPassword = updatePasswordDTO.getPassword();
        String reNewPassword = updatePasswordDTO.getRePassword();
        if (!newPassword.equals(reNewPassword)) {
            return Result.error("两次密码不一致!");
        }
        merchantService.updatePassword(newPassword);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO) {
        merchantService.update(userUpdateDTO);
        return Result.success();
    }

}
