package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.DishFlavor;

@Mapper
public interface FlavorMapper {

    void insertFlavors(List<DishFlavor> flavors);

    void deleteByDishids(List<Long> ids);

    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    @Delete("delete from dish_flavor where id = #{id}")
    void deleteByDishid(Long id);
}