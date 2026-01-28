package com.sky.controller.Task;

import com.sky.service.OrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class Task {
    @Resource
    private OrderService orderService;
    @Scheduled(cron = "0 * * * * *")
    @ApiOperation("订单超时")
    public void cancelTimeOut(){
        orderService.cancelTimeOut();
    }

    @Scheduled(cron = "* * 1 * * *")
    @ApiOperation("订单强制完成")
    public void completeTimeOut(){
        orderService.completeTimeOut();
        log.info("开始扫描");
    }
}
