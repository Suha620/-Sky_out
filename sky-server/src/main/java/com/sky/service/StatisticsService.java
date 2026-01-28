package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface StatisticsService {
    TurnoverReportVO turnoverStatistics( @DateTimeFormat(pattern = "yyyy-MM-dd")
                                         LocalDate begin,
                                         @DateTimeFormat(pattern = "yyyy-MM-dd")
                                         LocalDate end);

    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);
}
