package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController(value = "shopController")
@Slf4j
@RequestMapping("/user/shop")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus =(Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("状态:"+shopStatus);
        return Result.success(shopStatus);
    }
}
