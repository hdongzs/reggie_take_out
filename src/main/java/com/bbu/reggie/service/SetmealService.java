package com.bbu.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbu.reggie.dto.SetmealDto;
import com.bbu.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithSetmealDishes(SetmealDto setmealDto);

    public void deleteWithSetmealDishes(List<Long> ids);

    public SetmealDto queryWithSetmealDishes(Long setmealId);

    public void updateWithSetmealDishes(SetmealDto setmealDto);
}
