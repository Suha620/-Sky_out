package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Api("菜品开发")
@RequestMapping("/admin/dish")
@RestController
@Slf4j
public class DishController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;
    @PostMapping()
    @ApiOperation("新增菜品")
    public Result  save( @RequestBody  DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        this.cleanCache("dish_*");
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult>  page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping("")
    @ApiOperation("菜品操作")
    public Result Delete(@RequestParam List<Long> ids) {
        dishService.delete(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品")
    public Result<DishVO> getById(@PathVariable Integer id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation("修改菜单")
    public Result update(@RequestBody  DishDTO dishDTO){
        dishService.update(dishDTO);
        this.cleanCache("dish_*");
        return Result.success();
    }
    @PostMapping("status/{status}")
    @ApiOperation("起售/停售")
    public Result  enable_Disable(@RequestParam Integer id,@PathVariable Integer status){
        dishService.enable_Disable(id,status);
        this.cleanCache("dish_*");
        return Result.success();
    }
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
        log.info("Deleted cache keys: {}", keys); // Log the deleted keys for debugging
    }
}