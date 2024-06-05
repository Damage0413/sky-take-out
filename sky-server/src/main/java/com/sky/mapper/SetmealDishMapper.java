package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sky.anno.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;

@Mapper
public interface SetmealDishMapper {
    List<Long> getByDishid(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

}
