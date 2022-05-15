package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.common.CustomException;
import com.bbu.reggie.dto.SetmealDto;
import com.bbu.reggie.entity.Category;
import com.bbu.reggie.entity.Dish;
import com.bbu.reggie.entity.Setmeal;
import com.bbu.reggie.entity.SetmealDish;
import com.bbu.reggie.mapper.SetmealMapper;
import com.bbu.reggie.service.CategoryService;
import com.bbu.reggie.service.DishService;
import com.bbu.reggie.service.SetmealDishService;
import com.bbu.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServcieImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Override
    @Transactional
    public void saveWithSetmealDishes(SetmealDto setmealDto) {
        //保存套餐的信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish:setmealDishList){
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishList);
    }

    @Override
    @Transactional
    public void deleteWithSetmealDishes(List<Long> ids) {
        //判断，此删除的菜品列表中，是否有在售的套餐，有就不能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        long res = this.count(queryWrapper);
        if(res>0){
            throw new CustomException("有套餐在售，不能删除....");
        }
        //删除setmeal_dish表中的信息
        LambdaQueryWrapper<SetmealDish> queryWrapperDish = new LambdaQueryWrapper();
        queryWrapperDish.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapperDish);

        //删除套餐信息
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper();
        queryWrapper2.in(Setmeal::getId,ids);
        this.remove(queryWrapper2);

    }

    /**
     * 根据套餐id,查询套餐的详细信息，包括其中的菜品信息
     * @param setmealId
     */
    @Override
    public SetmealDto queryWithSetmealDishes(Long setmealId) {
        //根据id查询setmeal信息
        Setmeal setmeal = this.getById(setmealId);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询setmealId查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        //获取套餐类型的name
        Category category = categoryService.getById(setmeal.getCategoryId());
        setmealDto.setCategoryName(category.getName());
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /**
     * 修改套餐的信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithSetmealDishes(SetmealDto setmealDto) {
        //更新setmeal
        this.updateById(setmealDto);
        //删除setmeal_Dish中关于此套餐的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //插入新的setmealDish信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

}
