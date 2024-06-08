package com.sky.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("添加菜品操作")
    @CacheEvict(cacheNames = "dish", key = "#dishDTO.categoryId")
    public Result<String> insertDish(@RequestBody DishDTO dishDTO) {
        dishService.insertDish(dishDTO);
        log.info("新增菜品");
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询菜品操作")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除菜品操作")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result<String> deleteByIds(@RequestParam List<Long> ids) {
        dishService.deleteByIds(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改菜品操作")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result<String> updateDishWithFlavor(@RequestBody DishDTO dishDTO) {
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询回显菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品状态操作")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result<String> updateDishStatus(@PathVariable Integer status, Long id) {
        dishService.updateDishStatus(status, id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishsByCategoryId(Long categoryId) {
        List<Dish> dishlist = dishService.getDishsByCategoryId(categoryId);
        return Result.success(dishlist);
    }

}
