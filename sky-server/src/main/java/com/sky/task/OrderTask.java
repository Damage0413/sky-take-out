package com.sky.task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?") // 每分钟一次
    public void processTimeoutOrder() {
        log.info("定时处理已超时订单：{}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderTime = now.plusMinutes(-15);
        List<Orders> list = orderMapper.getTimeoutOrder(orderTime);
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(x -> {
                x.setStatus(Orders.CANCELLED);
                x.setCancelTime(now);
                x.setCancelReason("订单支付超时");
                orderMapper.update(x);
            });
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨一点
    public void processDeliveryOrder() {
        log.info("定时处理派送中订单：{}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderTime = now.plusHours(-1);
        List<Orders> list = orderMapper.getDeliveryOrder(orderTime);
        if (list != null && !list.isEmpty()) {
            list.stream().forEach(x -> {
                x.setStatus(Orders.COMPLETED);
                orderMapper.update(x);
            });
        }
    }
}
