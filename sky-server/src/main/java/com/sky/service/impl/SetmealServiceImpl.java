package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public void insertSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insertSetmeal(setmeal);

        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishs = setmealDTO.getSetmealDishes();
        if (!setmealDishs.isEmpty()) {
            setmealDishs.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.insertDishs(setmealDishs);
        }

    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = new PageResult();
        setmealPageQueryDTO.setStart((setmealPageQueryDTO.getPage() - 1) * setmealPageQueryDTO.getPageSize());

        Long total = setmealMapper.count(setmealPageQueryDTO);

        List<SetmealVO> setmealVOs = setmealMapper.page(setmealPageQueryDTO);
        pageResult.setTotal(total);
        pageResult.setRecords(setmealVOs);
        return pageResult;
    }

    @Override
    @Transactional
    public void deleteSetmealByIds(List<Long> ids) {
        List<Setmeal> setmeals = setmealMapper.getByIds(ids);
        if (!setmeals.isEmpty()) {
            setmeals.forEach(setmeal -> {
                // 起售中的套餐不能删除
                if (setmeal.getStatus() == StatusConstant.ENABLE) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                }
            });
        }
        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishs = setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishs);
        return setmealVO;

    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        // 先删除再添加
        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishMapper.insertDishs(setmealDishes);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        // 判断起售的套餐中是否有停售的菜品
        if (status == StatusConstant.ENABLE) {
            List<Dish> dishs = dishMapper.getBySetmealId(id);
            if (!dishs.isEmpty()) {
                dishs.forEach(dish -> {
                    if (dish.getStatus() == StatusConstant.DISABLE) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealMapper.update(setmeal);
    }

}
