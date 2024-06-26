package com.sky.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;

@Mapper
public interface OrderDetailMapper {

    void insertDetails(List<OrderDetail> orderDetails);

    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime);

}
