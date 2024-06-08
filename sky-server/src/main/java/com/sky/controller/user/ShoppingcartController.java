package com.sky.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车相关接口")
@Slf4j
public class ShoppingcartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("add")
    @ApiOperation("添加购物车操作")
    public Result<String> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加菜品到购物车，{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车操作")
    public Result<List<ShoppingCart>> showcart() {
        List<ShoppingCart> shoppingCarts = shoppingCartService.showcart();
        return Result.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车操作")
    public Result<String> clean() {
        shoppingCartService.clean();
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("删除购物车中菜品操作")
    public Result<String> sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车中菜品,{}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
