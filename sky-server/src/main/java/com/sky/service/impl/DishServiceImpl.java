package com.sky.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private FlavorMapper flavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void insertDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors.size() > 0 && flavors != null) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            flavorMapper.insertFlavors(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        Long total = dishMapper.total(dishPageQueryDTO);

        int start = (dishPageQueryDTO.getPage() - 1) * dishPageQueryDTO.getPageSize();
        dishPageQueryDTO.setStart(start);

        List<DishVO> page = dishMapper.page(dishPageQueryDTO);

        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(page);
        return pageResult;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // 查询是否有启售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 查询是否有套餐中的菜品
        List<Long> setmealIds = setmealDishMapper.getByDishid(ids);
        if (!setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品
        dishMapper.deleteByIds(ids);
        // 删除口味
        flavorMapper.deleteByDishids(ids);

    }

    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 更新菜品
        dishMapper.updateDish(dish);

        Long dishId = dishDTO.getId();
        // 删除原来的口味
        flavorMapper.deleteByDishid(dishId);
        // 添加新的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            flavorMapper.insertFlavors(flavors);
        }
    }

    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = flavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;

    }

    @Override
    public void updateDishStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateDish(dish);

        // 更改对应的套餐
        if (status == StatusConstant.DISABLE) {
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            List<Long> setmealIds = setmealDishMapper.getByDishid(dishIds);
            if (!setmealIds.isEmpty()) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealDishMapper.update(setmeal);

                }
            }
        }
    }

    @Override
    public List<Dish> getDishsByCategoryId(Long id) {

        return dishMapper.getDishsByCategoryId(id);

    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            // 根据菜品id查询对应的口味
            List<DishFlavor> flavors = flavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
