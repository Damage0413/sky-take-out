package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.anno.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * 
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insertDish(Dish dishDTO);

    List<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    Long total(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    void deleteByIds(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dishDTO);

    List<Dish> getDishsByCategoryId(Long categoryId);

    @Select("select d.* from dish d left join setmeal_dish s on d.id = s.dish_id where s.setmeal_id = #{id}")
    List<Dish> getBySetmealId(Long id);

    List<Dish> list(Dish dish);

}
