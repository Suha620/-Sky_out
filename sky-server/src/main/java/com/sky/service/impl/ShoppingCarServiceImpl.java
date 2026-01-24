package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCarMapper;
import com.sky.service.ShoppingCarService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class ShoppingCarServiceImpl implements ShoppingCarService {
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private ShoppingCarMapper shoppingCarMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        //判断
        if(list!=null && list.size()>0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCarMapper.updateBydId(cart);
        }else{
            if (shoppingCartDTO.getDishId()!=null) {
                Long dishId = shoppingCartDTO.getDishId();
                Dish dish = dishMapper.getById(Math.toIntExact(dishId));
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getBySetmealId(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);
                shoppingCarMapper.insert(shoppingCart);

        }
    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void clean() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        shoppingCarMapper.clean(shoppingCart);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        ShoppingCart shoppingCart1 = list.get(0);
        shoppingCart1.setNumber(list.get(0).getNumber()-1);
        if(list.get(0).getNumber()>1){
            shoppingCarMapper.updateBydId(shoppingCart1);
        }
        else{
           shoppingCarMapper.cut(shoppingCart1);
        }
    }
}
