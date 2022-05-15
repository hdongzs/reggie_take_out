package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.common.CustomException;
import com.bbu.reggie.dto.DishDto;
import com.bbu.reggie.entity.Dish;
import com.bbu.reggie.entity.DishFlavor;
import com.bbu.reggie.mapper.DishMapper;
import com.bbu.reggie.service.DishFlavorService;
import com.bbu.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存菜品的同时，将菜品的口味信息进行保存
     * @param dishDto 封装菜品信息，和菜品口味的对象
     */
    @Override
    @Transactional
    public void saveWithFlavors(DishDto dishDto) {
        //保存dish
        this.save(dishDto);
        Long dishId = dishDto.getId();
        //给每个dishFlavor添加dishId
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor:flavors){
            flavor.setDishId(dishId);
        }
        //保存DishFlavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据菜品id，查询菜品的信息，和菜品的口味信息
     * @param dishId 菜品的id
     * @return
     */
    @Override
    public DishDto queryWithFlavors(Long dishId) {
        //查询dish信息
        Dish dish = this.getById(dishId);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //根据dishId查询flavors
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 根据封装的菜品信息对象，修改菜品的信息
     * @param dishDto 封装菜品信息，和菜品口味信息的对象
     */
    @Override
    @Transactional
    public void editWithFlavors(DishDto dishDto) {
        //修改Dish信息
        this.updateById(dishDto);
        //根据dishId删除此菜品的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //保存口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor:flavors){
            flavor.setId(null);
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据菜品id删除，菜品及其相关的口味信息
     * @param ids 批量删除的菜品id
     */
    @Override
    @Transactional
    public void deleteWithFlavors(List<Long> ids) {
        //当菜品处于停售状态时，才能删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        long count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("商品处于在售状态，不能删除......");
        }
        //删除口味信息
        LambdaUpdateWrapper<DishFlavor> flavorWrapper = new LambdaUpdateWrapper<>();
        flavorWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(flavorWrapper);

        //删除菜品信息
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.in(Dish::getId,ids);
        this.remove(updateWrapper);
    }
}
