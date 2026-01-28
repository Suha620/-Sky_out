package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.StatisticsService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate>dateList = new ArrayList();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer>newUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime localDateTime_begin = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime localDateTime_end = LocalDateTime.of(localDate, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("localDateTime_begin",localDateTime_begin);
            map.put("localDateTime_end",localDateTime_end);
            Integer countUser = userMapper.countUser(map);
            newUserList.add(countUser);
            totalUserList.add(userMapper.getAllUser());
        }
        return new UserReportVO(StringUtils.join(dateList,","),
                StringUtils.join(totalUserList,","),
                StringUtils.join(newUserList,","));
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
            LocalDateTime localDateTime_begin = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime localDateTime_end = LocalDateTime.of(end, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("localDateTime_begin",localDateTime_begin);
            map.put("localDateTime_end",localDateTime_end);
        List<GoodsSalesDTO> goodsSalesDTOS = orderDetailMapperr.top10(map);
        List<String> collect = goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> collect1 = goodsSalesDTOS.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return  new SalesTop10ReportVO(StringUtils.join(collect,","),StringUtils.join(collect1,","));
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        Integer talOrderCount = orderMapper.toTalOrderCount();
        Integer orderValidateCount = orderMapper.toTalOrderValidateCount();
//        Double orderCompletionRate = (double) (BigDecimal.valueOf(orderValidateCount)/talOrderCount);
        BigDecimal bigDecimal = BigDecimal.valueOf(orderValidateCount);
        BigDecimal bigDecimal1 = BigDecimal.valueOf(talOrderCount);
        BigDecimal bigDecimal2 = bigDecimal.divide(bigDecimal1, 4, RoundingMode.HALF_UP)
                .setScale(4, RoundingMode.HALF_UP);
        Double orderCompletionRate = bigDecimal2.doubleValue();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> vaildOrderCountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime localDateTime_begin = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime localDateTime_end = LocalDateTime.of(localDate, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("localDateTime_begin",localDateTime_begin);
            map.put("localDateTime_end",localDateTime_end);
            orderCountList.add(orderMapper.toTalDayOrderCount(map));
            map.put("status", Orders.COMPLETED);
            vaildOrderCountList.add(orderMapper.toTalDayOrderCount(map));
        }

        return new OrderReportVO(StringUtils.join(dateList),StringUtils.join(orderCountList),StringUtils.join(vaildOrderCountList),talOrderCount,orderValidateCount,orderCompletionRate);
    }

    @Resource
    private OrderDetailMapper orderDetailMapperr;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();
        list.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            list.add(begin);
        }
        List<Double> amount_list = new ArrayList<>();
        for (LocalDate localDate : list) {
            LocalDateTime localDateTime_begin = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime localDateTime_end = LocalDateTime.of(localDate, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("localDateTime_begin",localDateTime_begin);
            map.put("localDateTime_end",localDateTime_end);
            map.put("status", Orders.COMPLETED);
            amount_list.add(orderMapper.sumByMap(map)==null? 0.0 : orderMapper.sumByMap(map));
        }
        return new TurnoverReportVO(StringUtils.join(list,","),StringUtils.join(amount_list,","));
    }
}
