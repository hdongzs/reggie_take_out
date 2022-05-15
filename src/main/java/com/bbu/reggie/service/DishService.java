package com.bbu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bbu.reggie.dto.DishDto;
import com.bbu.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavors(DishDto dishDto);

    public DishDto queryWithFlavors(Long dishId);

    public void editWithFlavors(DishDto dishDto);

    public void deleteWithFlavors(List<Long> ids);

}
