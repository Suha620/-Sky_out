package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("admin/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch( OrdersPageQueryDTO ordersPageQueryDTO ){
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        log.info(pageResult.getRecords().toString());
        return Result.success(pageResult);
    }
}
