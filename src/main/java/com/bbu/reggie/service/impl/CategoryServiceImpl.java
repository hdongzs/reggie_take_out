package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.common.CustomException;
import com.bbu.reggie.entity.Category;
import com.bbu.reggie.entity.Dish;
import com.bbu.reggie.entity.Setmeal;
import com.bbu.reggie.mapper.CategoryMapper;
import com.bbu.reggie.mapper.DishMapper;
import com.bbu.reggie.mapper.SetmealMapper;
import com.bbu.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void remove(long id){
        //根据id删除菜品和套餐类型
        //在删除前，要判断此菜品类型是否已经和菜品或者套餐关联，若已经关联，则不能删除
        //根据菜品类型查询是否存在菜品
        QueryWrapper<Dish> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("category_Id",id);
        Long count = dishMapper.selectCount(wrapper1);
        if(count>0){
            //已经关联了菜品
            throw new CustomException("类型已经与菜品关联，不能删除....");
        }

        QueryWrapper<Setmeal> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("category_Id",id);
        Long count2 = setmealMapper.selectCount(wrapper2);
        if(count2>0){
            throw new CustomException("类型已与套餐关联，不能删除....");
        }
    }
}
