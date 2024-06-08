package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前商品是否存在在购物车中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long currentid = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentid);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && !list.isEmpty()) {
            // 数量加一
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        } else {
            // 添加到购物车中
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setNumber(1);
            } else {
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setNumber(1);
            }
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> showcart() {
        Long id = BaseContext.getCurrentId();
        log.info("查询购物车内容，用户id是{}", id);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.showcart(id);
        return shoppingCarts;

    }

    @Override
    public void clean() {
        Long id = BaseContext.getCurrentId();
        log.info("清空购物车内容，用户id是{}", id);
        shoppingCartMapper.clean(id);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long currentid = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentid);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && !list.isEmpty()) {
            // 数量减一
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() - 1);
            if (cart.getNumber() == 0) {
                shoppingCartMapper.deleteById(cart.getId());
            } else {
                shoppingCartMapper.update(cart);
            }
        }

    }

}