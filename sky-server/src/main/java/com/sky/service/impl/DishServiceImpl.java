package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && !flavors.isEmpty()){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> dishVOPageHelper = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishVOPageHelper.getTotal(),dishVOPageHelper.getResult());
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
          Dish  dish = dishMapper.findStatus(id);
            if(dish.getStatus()== StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> setMealDishIds = setMealDishMapper.getSetMealDishIds(ids);

        if (!setMealDishIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.delete(ids);
        dishFlavorMapper.delete(ids);
    }

    @Override
    public DishVO getById(Integer id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor>dishFlavorList= dishFlavorMapper.getByDishId(id);
        DishVO dishVO= new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        dishFlavorMapper.delete(Collections.singletonList(dishDTO.getId()));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && !flavors.isEmpty()){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Transactional
    @Override
    public void enable_Disable(Integer id,Integer status) {
        List<Long>list_id = new ArrayList<>();
        list_id.add(Long.valueOf(id));
        if ((status == StatusConstant.ENABLE)) {
            dishMapper.enable_Disable(id,StatusConstant.DISABLE);
            List<Long>list = setMealDishMapper.getSetMealDishIds(list_id);
            setmealMapper.enable_Disable(list,status);
        } else {
            dishMapper.enable_Disable(id,StatusConstant.ENABLE);
            List<Long>list = setMealDishMapper.getSetMealDishIds(list_id);
            setmealMapper.enable_Disable(list,status);
        }
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(Math.toIntExact(d.getId()));

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}
