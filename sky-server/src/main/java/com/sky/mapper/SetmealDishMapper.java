package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import com.sky.anno.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;

@Mapper
public interface SetmealDishMapper {
    List<Long> getByDishid(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    void insertDishs(List<SetmealDish> setmealDishs);

    void deleteBySetmealIds(List<Long> ids);

    List<SetmealDish> getBySetmealId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

}
