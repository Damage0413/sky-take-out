package com.sky.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * 
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * 
     * @param orders
     */
    void update(Orders orders);

    Long total(Integer start, Integer pageSize, Integer status);

    List<Orders> page(Integer start, Integer pageSize, Integer status);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders getById(Long id);

    Long totalByadmin(OrdersPageQueryDTO ordersPageQueryDTO);

    List<Orders> pageByadmin(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = 1 and order_time < #{orderTime}")
    List<Orders> getTimeoutOrder(LocalDateTime orderTime);

    @Select("select * from orders where status = 4 and order_time < #{orderTime}")
    List<Orders> getDeliveryOrder(LocalDateTime orderTime);

    @Select("select sum(amount) from orders where order_time between #{beginTime} and #{endTime} and status = 5")
    Double sumByData(LocalDateTime beginTime, LocalDateTime endTime);

    Integer countByDateatime(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

}
