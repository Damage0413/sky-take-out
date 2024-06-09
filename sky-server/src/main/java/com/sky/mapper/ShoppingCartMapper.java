package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.ShoppingCart;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void update(ShoppingCart cart);

    @Insert("insert into shopping_cart (name,image,user_id,dish_id,setmeal_id,dish_flavor,number,amount,create_time)"
            +
            "values" +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{id}")
    List<ShoppingCart> showcart(Long id);

    @Delete("delete from shopping_cart where user_id = #{id}")
    void clean(Long id);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    void insertBatch(List<ShoppingCart> shoppingCarts);

}
