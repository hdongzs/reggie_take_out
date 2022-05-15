package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbu.reggie.common.R;
import com.bbu.reggie.entity.Category;
import com.bbu.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 菜品类型控制层类
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加、更新菜品、套餐类型方法
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category){
        categoryService.save(category);
        return R.success("success");
    }

    /**
     * 分页查询菜品类型信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page,Integer pageSize){
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改菜品类型信息
     * @param category
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("success");
    }

    /**
     * 删除菜品类型信息，如果该类型已经与菜品或套餐关联，则删除失败
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("success");
    }

    @GetMapping("/list")
    public R<List<Category>> listCategory(Integer type){
        //根据type查询category
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(type!=null,"type",type);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
