package com.sky.service;

import java.util.List;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

public interface SetmealService {

    void insertSetmeal(SetmealDTO setmealDTO);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmealByIds(List<Long> ids);

    SetmealVO getById(Long id);

    void update(SetmealDTO setmealDTO);

    void changeStatus(Integer status, Long id);

}
