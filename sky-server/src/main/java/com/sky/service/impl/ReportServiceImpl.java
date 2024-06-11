package com.sky.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
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

    @Autowired
    private WorkspaceService workspaceService;

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

    @Override
    public void export(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().plusDays(-30);
        LocalDate end = LocalDate.now().plusDays(-1);

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        // 使用POI写入
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(resourceAsStream);
            // 填充数据
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);

            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                // 查询某一天的营业数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));

                // 获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 使用输出流下载文件
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            excel.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
