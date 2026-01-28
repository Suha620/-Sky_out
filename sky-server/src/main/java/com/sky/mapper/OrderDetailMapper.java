package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail>list);

    @Select("select * from order_detail where order_id = #{Id} ")
    List<OrderDetail> getOrderDetailList(Long Id);

     List<GoodsSalesDTO> top10(Map<String,Object> map);
}
