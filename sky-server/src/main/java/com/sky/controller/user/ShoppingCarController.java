package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCarMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@Api(tags = "购物车接口")
@RequestMapping("/user/shoppingCart")
public class ShoppingCarController {
    @Resource
    private ShoppingCarService shoppingCarService;
    @PostMapping("/add")
    public Result add(@RequestBody  ShoppingCartDTO shoppingCartDTO){
        shoppingCarService.add(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCarService.list();
        return Result.success(list);
    }
    @DeleteMapping("/clean")
    public Result clean(){
        shoppingCarService.clean();
        return Result.success();
    }
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCarService.sub(shoppingCartDTO);
        return Result.success();
    }
}
