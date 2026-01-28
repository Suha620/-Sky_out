package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Builder
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw  new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        if(list.isEmpty() ){
           throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
       Orders  orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        orders.setOrderTime(LocalDateTime.now());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orderMapper.insertOrders(orders);
        List <OrderDetail>orderDetail_List = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetail_List.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetail_List);
        shoppingCarMapper.clean(shoppingCart);
        OrderSubmitVO build = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return build;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单

        //发现没有将支付时间 check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();

        //获取订单号码
        String orderNumber = ordersPaymentDTO.getOrderNumber();

        log.info("调用updateStatus，用于替换微信支付更新数据库状态的问题");
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderNumber);

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号:"+outTradeNo);
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> orders = orderMapper.conditionSearch(ordersPageQueryDTO);
        Page<OrderVO>conditionSearch_list = new Page<>();
        for (Orders order : orders) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);
            orderVO.setOrderDetailList(this.getOrderDetailList(order.getId()));
            orderVO.setOrderDishes(this.getOrderDishes(order.getId()));
            conditionSearch_list.add(orderVO);
        }

        return new PageResult(orders.getTotal(),conditionSearch_list);

        }

    @Override
    public OrderVO conditionSearchById(String id) {
        Orders orders = orderMapper.conditionSearchByID(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(this.getOrderDetailList(orders.getId()));
        orderVO.setOrderDishes(this.getOrderDishes(orders.getId()));
        return orderVO;
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetailList = orderDetailMapper.getOrderDetailList(id);
        List<ShoppingCart>shoppingCarts = new ArrayList<>();
        for (OrderDetail copiedOrderDetail : orderDetailList) {
            ShoppingCart copyShoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(copiedOrderDetail,copyShoppingCart);
            copyShoppingCart.setUserId(BaseContext.getCurrentId());
            copyShoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCarts.add(copyShoppingCart);
        }
        shoppingCarMapper.insertBatch(shoppingCarts);
    }

    @Override
    public OrderStatisticsVO getOrderStatisticsVO() {
        List<Orders> orders = orderMapper.getOrderStatisticsVO();
        Integer CONFIRMED=0;
        Integer TO_BE_CONFIRMED=0;
        Integer DELIVERY_IN_PROGRESS=0;
        for (Orders order : orders) {
            if(order.getStatus().equals(Orders.CONFIRMED)){
                    CONFIRMED+=1;
            } else if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
                    TO_BE_CONFIRMED+=1;
            } else if (order.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
                    DELIVERY_IN_PROGRESS+=1;
            }
        }
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(CONFIRMED);
        orderStatisticsVO.setToBeConfirmed(TO_BE_CONFIRMED);
        orderStatisticsVO.setDeliveryInProgress(DELIVERY_IN_PROGRESS);
        return  orderStatisticsVO;
    }

    @Override
    public void userCancelById(Long id) {
        Orders orders = orderMapper.conditionSearchByID(String.valueOf(id));
        if(orders.getStatus().equals(Orders.PENDING_PAYMENT)||orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelReason("用户取消");
            orders.setCancelTime(LocalDateTime.now());
            orderMapper.cancelById(orders);
            orderMapper.update(orders);
        } else if (orders.getStatus()>Orders.TO_BE_CONFIRMED){
            throw new com.sky.exception.OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(3);
        orderMapper.confirm(ordersConfirmDTO);
    }

    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.conditionSearchByID(String.valueOf(ordersRejectionDTO.getId()));
        if(orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            orderMapper.reject(ordersRejectionDTO);
        }
        else {
            throw new com.sky.exception.OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.conditionSearchByID(String.valueOf(id));
        if(orders.getStatus().equals(Orders.CONFIRMED)){
            orderMapper.delivery(id);
        }
        else {
            throw new com.sky.exception.OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.conditionSearchByID(String.valueOf(id));
        if(orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            orderMapper.complete(id);
        }
        else {
            throw new com.sky.exception.OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.conditionSearchByID(String.valueOf(ordersCancelDTO.getId()));
        if(orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            orderMapper.cancel(ordersCancelDTO);
        }
        else {
            throw new com.sky.exception.OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public void cancelTimeOut() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        Integer status = Orders.PENDING_PAYMENT;
        List<Orders> orders = orderMapper.conditionTimeOut(status, time);
        for (Orders order : orders) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("订单超时");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        }
    }

    @Override
    public void completeTimeOut() {
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        Integer status = Orders.DELIVERY_IN_PROGRESS;
        List<Orders> orders = orderMapper.completeTimeOut(status, time);
        for (Orders order : orders) {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
            log.info("完成了\t"+order.getId()+"订单");
        }
    }

    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.conditionSearchByID(String.valueOf(id));
        if(orders!=null){
           Map<String,Object> map = new HashMap();
           map.put("type",2);
           map.put("orderId",orders.getId());
           map.put("content",orders.getNumber());
            String jsonString = JSONObject.toJSONString(map);
            webSocketServer.sendToAllClient(jsonString);
        }
        else {
            throw new com.sky.exception.OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
    }

    @Override
    public PageResult searchHistory(Integer page,Integer pageSize,Integer status) {
        PageHelper.startPage(page,pageSize);
        Page<Orders> orders = orderMapper.searchHistory(BaseContext.getCurrentId());
        Page<OrderVO>orderVO = new Page<>();
        for (Orders order : orders) {
            OrderVO orderVO1 = new OrderVO();
            BeanUtils.copyProperties(order,orderVO1);
            orderVO1.setOrderDetailList(this.getOrderDetailList(order.getId()));
            orderVO1.setOrderDishes(this.getOrderDishes(order.getId()));
            orderVO.add(orderVO1);
        }
        return new PageResult(orders.getTotal(),orderVO);
    }

    public List<OrderDetail> getOrderDetailList(Long Id){
        return orderDetailMapper.getOrderDetailList(Id);
    }
    public String getOrderDishes(Long Id){
        String collect = orderDetailMapper.getOrderDetailList(Id).stream().map(x ->
                x.getName() + "*" + x.getNumber() + "\t"
        ).collect(Collectors.joining());
        return collect;
    }
}