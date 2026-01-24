package com.sky.mapper;

import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByDishId(Integer id);

    void insertBatch(List<DishFlavor> flavors);

    void delete(List<Long> idTemp);
}
