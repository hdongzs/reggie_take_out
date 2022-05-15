package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbu.reggie.common.R;
import com.bbu.reggie.dto.SetmealDto;
import com.bbu.reggie.entity.Setmeal;
import com.bbu.reggie.service.CategoryService;
import com.bbu.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 保存添加的套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithSetmealDishes(setmealDto);
        return R.success("成功");
    }

    /**
     * 分页模糊查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageInfoDto = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //分页查询套餐信息
        setmealService.page(pageInfo,queryWrapper);
        //拷贝分页信息到setmealPage
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        SetmealDto setmealDto = null;
        for (Setmeal setmeal:pageInfo.getRecords()){
            setmealDto = new SetmealDto();
            //将套餐的基本信息复制到setmealDto
            BeanUtils.copyProperties(setmeal,setmealDto);
            setmealDto.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
            setmealDtoList.add(setmealDto);
        }
        pageInfoDto.setRecords(setmealDtoList);
        return R.success(pageInfoDto);
    }

    /**
     * 批量修改套餐的售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.set(Setmeal::getStatus,status);
        setmealService.update(updateWrapper);
        return R.success("成功");
    }

    /**
     * 根据id批量删除套餐信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        setmealService.deleteWithSetmealDishes(ids);
        return R.success("成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealWithDishes(@PathVariable("id") Long setmealId){
        SetmealDto setmealDto = setmealService.queryWithSetmealDishes(setmealId);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> editSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithSetmealDishes(setmealDto);
        return R.success("成功");
    }

    @GetMapping("list")
    public R<List<Setmeal>> querySetmealWithDishes(Setmeal setmeal){
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
        //查询setmeal信息
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId)
                .eq(status!=null,Setmeal::getStatus,status);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        return R.success(setmealList);
    }
 }
