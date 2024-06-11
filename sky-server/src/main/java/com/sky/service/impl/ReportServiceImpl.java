package com.sky.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            dates.add(begin);
            begin = begin.plusDays(1);
        }
        dates.add(end);
        String join = StringUtils.join(dates, ",");

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate data : dates) {
            LocalDateTime beginTime = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(data, LocalTime.MAX);
            Double turnover = orderMapper.sumByData(beginTime, endTime);
            if (turnover == null) {
                turnover = 0.0;
            }
            turnoverList.add(turnover);

        }
        String turnoverString = StringUtils.join(turnoverList, ",");

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(join);
        turnoverReportVO.setTurnoverList(turnoverString);
        return turnoverReportVO;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            dates.add(begin);
            begin = begin.plusDays(1);
        }
        dates.add(end);
        String join = StringUtils.join(dates, ",");

        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate data : dates) {
            LocalDateTime beginTime = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(data, LocalTime.MAX);
            Integer totalUser = userMapper.countByDateTime(null, endTime);
            Integer newUser = userMapper.countByDateTime(beginTime, endTime);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        String totalUSerString = StringUtils.join(totalUserList, ",");
        String newUSerString = StringUtils.join(newUserList, ",");

        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(join);
        userReportVO.setTotalUserList(totalUSerString);
        userReportVO.setNewUserList(newUSerString);
        return userReportVO;
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        while (!begin.equals(end)) {
            dates.add(begin);
            begin = begin.plusDays(1);
        }
        dates.add(end);
        String join = StringUtils.join(dates, ",");

        List<Integer> totalOrdersList = new ArrayList<>();
        List<Integer> fininshedOrdersList = new ArrayList<>();
        for (LocalDate data : dates) {
            LocalDateTime beginTime = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(data, LocalTime.MAX);

            Integer totalOrder = orderMapper.countByDateatime(beginTime, endTime, null);
            Integer finishOrder = orderMapper.countByDateatime(beginTime, endTime, 5);

            totalOrdersList.add(totalOrder);
            fininshedOrdersList.add(finishOrder);
        }

        String totalOrdersString = StringUtils.join(totalOrdersList, ",");
        String finishedOrdersString = StringUtils.join(fininshedOrdersList, ",");

        Integer sumOrders = totalOrdersList.stream().reduce(Integer::sum).get();
        Integer sumfinishedOrders = fininshedOrdersList.stream().reduce(Integer::sum).get();

        Double orderFinishedRate = 0.0;
        if (sumOrders != 0) {
            orderFinishedRate = sumfinishedOrders.doubleValue() / sumOrders.doubleValue();
        }

        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(join);
        orderReportVO.setTotalOrderCount(sumOrders);
        orderReportVO.setOrderCompletionRate(orderFinishedRate);
        orderReportVO.setOrderCountList(totalOrdersString);
        orderReportVO.setValidOrderCount(sumfinishedOrders);
        orderReportVO.setValidOrderCountList(finishedOrdersString);
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO salesTop10ReportVO(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = orderDetailMapper.getTop10(beginTime, endTime);
        List<String> names = top10.stream().map(x -> x.getName()).collect(Collectors.toList());
        List<Integer> numbers = top10.stream().map(x -> x.getNumber()).collect(Collectors.toList());

        String nameString = StringUtils.join(names, ",");
        String numbersString = StringUtils.join(numbers, ",");
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(nameString);
        salesTop10ReportVO.setNumberList(numbersString);
        return salesTop10ReportVO;
    }
}
