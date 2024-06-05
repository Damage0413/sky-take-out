package com.sky.service;

import java.util.List;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

public interface DishService {

    void insertDish(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void deleteByIds(List<Long> ids);

    void updateDishWithFlavor(DishDTO dishDTO);

    DishVO getById(Long id);

    void updateDishStatus(Integer status, Long id);

    List<Dish> getDishsByCategoryId(Long id);

}
