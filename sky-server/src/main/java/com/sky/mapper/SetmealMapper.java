package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    void enable_Disable(List<Long> list,Integer status);
    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 更新套餐信息
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    /**
     * 根据套餐id删除绑定在套餐上的菜品
     * @param id
     */
    @Delete("delete from setmeal_dish where id=#{id}")
    void deleteSetmealDish(Long id);

    /**
     * 批量插入套餐绑定的菜品
     * @param setmealDishes
     */
    void insertBatchSetmealDish(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id获取套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id=#{id}")
    Setmeal getBySetmealId(Long id);

    /**
     * 根据套餐id获取套餐菜品绑定关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getSetmealDishById(Long id);

    /**
     * 根据套餐id批量删除套餐
     * @param ids
     */
    void batchDeleteSetmeal(ArrayList<Long> ids);

    /**
     * 插入套餐信息
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);
}
