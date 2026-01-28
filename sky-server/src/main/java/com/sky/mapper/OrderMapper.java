package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus
     * @param orderPaidStatus
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} " +
            "where number = #{orderNumber}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, String orderNumber);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */

    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    void insertOrders(Orders orders);

    @Select("select * from orders  where id = #{id}") /* order by delivery_time desc")*/
    Orders conditionSearchByID(String id);

    @Update("update orders set status = #{status} , cancel_reason = #{cancelReason}, cancel_time=#{cancelTime} where id = #{id}")
    void cancelById(Orders orders);

    @Select("select * from orders order by delivery_time desc")
    List<Orders> getOrderStatisticsVO();


    @Select("select * from orders where user_id=#{user_id} order by estimated_delivery_time desc")
    Page<Orders> searchHistory(Long user_id);

    @Update("update orders set status = #{status} where id = #{id} ")
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    @Update("update orders set status = 6 , cancel_reason=#{rejectionReason} ,cancel_time=now() where id = #{id} ")
    void reject(OrdersRejectionDTO ordersRejectionDTO);

    @Update("update orders set status = 4  where id = #{id} ")
    void delivery(Long id);

    @Update("update orders set status = 5,delivery_time=now()  where id = #{id} ")
    void complete(Long id);

    @Update("update orders set status = 6, cancel_reason = #{ cancelReason} where id = #{id} ")
    void cancel(OrdersCancelDTO ordersCancelDTO);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> conditionTimeOut(Integer status, LocalDateTime time);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> completeTimeOut(Integer status, LocalDateTime time);


    Double sumByMap(Map<String, Object> map);

    List<Orders> top10(Map<String, Object> map);

    @Select("select count(id)  from orders")
    Integer toTalOrderCount();

    @Select("select count(id) from orders where status = 5")
    Integer toTalOrderValidateCount();

    Integer toTalDayOrderCount(Map<String, Object> map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
