package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.anno.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * 
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(value = OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    Long count(SetmealPageQueryDTO setmealPageQueryDTO);

    List<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    List<Setmeal> getByIds(List<Long> ids);

    void deleteByIds(List<Long> ids);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

}