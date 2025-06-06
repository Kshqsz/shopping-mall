package cn.edu.usts.cs2022.controller;

import cn.edu.usts.cs2022.mapper.FavoriteMapper;
import cn.edu.usts.cs2022.pojo.po.Result;
import cn.edu.usts.cs2022.pojo.vo.client.ProductClientSimpleVo;
import cn.edu.usts.cs2022.service.FavoriteService;
import cn.edu.usts.cs2022.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteMapper favoriteMapper;

    @PostMapping("/{id}")
    public Result favorite(@PathVariable("id") Integer productId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        favoriteService.favorite(userId, productId);
        return Result.success();
    }

    @DeleteMapping()
    public Result cancelFavourite(@RequestParam List<Integer> ids) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        favoriteService.cancelFavourite(userId, ids);
        return Result.success();
    }

    @GetMapping()
    public Result<List<ProductClientSimpleVo>> getFavorites() {
        List<ProductClientSimpleVo> favorites = favoriteService.getFavorites();
        return Result.success(favorites);
    }

    @PostMapping("/status")
    public Result check(@RequestParam Integer id){
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        Integer fa_id = favoriteMapper.checkStatus(id,userId);
        if (fa_id == null){
            return Result.success(false);
        }
        else {
            return Result.success(true);
        }
    }
}
