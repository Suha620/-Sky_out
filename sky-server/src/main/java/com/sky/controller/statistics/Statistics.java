package com.sky.controller.statistics;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.StatisticsService;
import com.sky.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;


@RestController
@RequestMapping("/admin/report")
public class Statistics {

    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private OrderService orderService;
    @GetMapping("turnoverStatistics")
    public Result<TurnoverReportVO>  turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        TurnoverReportVO turnoverReportVO = statisticsService.turnoverStatistics(begin,end);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("userStatistics")
    public Result<UserReportVO>userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        UserReportVO userReportVO = statisticsService.userStatistics(begin, end);
        return Result.success(userReportVO);
    }

     @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
             @DateTimeFormat(pattern = "yyyy-MM-dd")
             LocalDate begin,
             @DateTimeFormat(pattern = "yyyy-MM-dd")
             LocalDate end
     ){
         SalesTop10ReportVO salesTop10ReportVO = statisticsService.top10(begin, end);
         return Result.success(salesTop10ReportVO);
     }
     @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
             @DateTimeFormat(pattern = "yyyy-MM-dd")
             LocalDate begin,
             @DateTimeFormat(pattern = "yyyy-MM-dd")
             LocalDate end
     ){
         OrderReportVO orderReportVO = statisticsService.ordersStatistics(begin, end);
         return Result.success(orderReportVO);
     }
}
