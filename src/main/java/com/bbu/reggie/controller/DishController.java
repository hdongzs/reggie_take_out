package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbu.reggie.common.R;
import com.bbu.reggie.dto.DishDto;
import com.bbu.reggie.entity.Category;
import com.bbu.reggie.entity.Dish;
import com.bbu.reggie.entity.DishFlavor;
import com.bbu.reggie.service.CategoryService;
import com.bbu.reggie.service.DishFlavorService;
import com.bbu.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 添加菜品
     * @param dishDto 上传的菜品对象，包括菜品的口味的封装
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        //保存菜品信息
        int res = 0;
        try {
            dishService.saveWithFlavors(dishDto);
            res = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(res == 0){
            return R.error("网络繁忙，请稍后重试....");
        }
        return R.success("成功");
    }

    /**
     * 菜品的分页、模糊查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page,Integer pageSize,String name){

        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null && name!="",Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,queryWrapper);
        BeanUtils.copyProperties(dishPage,dtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> recordsDto = new ArrayList<>();
        for(Dish dish:records){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Category category = categoryService.getById(dishDto.getCategoryId());
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }
            recordsDto.add(dishDto);
        }
        dtoPage.setRecords(recordsDto);
        return R.success(dtoPage);
    }

    /**
     * 商品修改时，进行商品信息的查询
     * @param dishId
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDish(@PathVariable("id") Long dishId){
        //根据id查询菜品信息
        DishDto dishDto = dishService.queryWithFlavors(dishId);
        return R.success(dishDto);
    }

    /**
     * 修改菜品的信息
     * @param dishDto
     * @return
     */
    @PutMapping()
    public  R<String> editDish(@RequestBody DishDto dishDto){
        int res = 0;
        try{
            dishService.editWithFlavors(dishDto);
            res = 1;
        }catch(Exception e){
            e.printStackTrace();
        }
       if(res == 0){
           return R.error("系统繁忙，请稍后重试....");
       }
       return R.success("成功");
    }

    /**
     * 修改菜品的售卖状态
     * @param status
     * @param ids
     * @return
     */

    @PostMapping("status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        //根据菜品id，修改状态
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dish::getId,ids);
        wrapper.set(Dish::getStatus,status);
        dishService.update(wrapper);
        return R.success("成功");
    }


    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> deleteById(@RequestParam List<Long> ids){
        dishService.deleteWithFlavors(ids);
        return R.success("成功");
    }

    @GetMapping("list")
    public R<List<DishDto>> list(Dish dish){
        //创建条件
         LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
         queryWrapper .eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId())
                 .eq(dish.getStatus()!=null,Dish::getStatus,dish.getStatus())
                 .like(dish.getName()!=null,Dish::getName,dish.getName())
                 .orderByAsc(Dish::getSort)
                 .orderByDesc(Dish::getUpdateTime);

         List<Dish> dishes = dishService.list(queryWrapper);
         //将dish中的数据复制到DishDto中
        List<DishDto> dishDtoList = new ArrayList<>();
        DishDto  dishDto = null;
        for (Dish iDish:dishes){
            dishDto = new DishDto();
            BeanUtils.copyProperties(iDish,dishDto);
            //查询类型名称
            dishDto.setCategoryName(categoryService.getById(dishDto.getCategoryId()).getName());
            //根据dishId查询菜品的口味信息
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
            flavorQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
            List<DishFlavor> flavors = dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(flavors);
            dishDtoList.add(dishDto);
        }
        return R.success(dishDtoList);
    }
}
