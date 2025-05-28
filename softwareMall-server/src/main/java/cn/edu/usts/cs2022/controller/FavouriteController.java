package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.service.FavouriteService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/favourite")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;
    @PostMapping("/{id}")
    public Result favourite(@PathVariable("id") Integer productId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        favouriteService.favourite(userId, productId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result cancelFavourite(@PathVariable("id") Integer productId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        favouriteService.cancelFavourite(userId, productId);
        return Result.success();
    }
}
