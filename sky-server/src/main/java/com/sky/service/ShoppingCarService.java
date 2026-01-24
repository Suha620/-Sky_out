package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShoppingCarService {
    public void add(ShoppingCartDTO shoppingCartDTO);

    public List<ShoppingCart> list();

    public void clean();

    public void sub(ShoppingCartDTO shoppingCartDTO);
}
