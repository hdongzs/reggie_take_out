package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.entity.DishFlavor;
import com.bbu.reggie.service.DishFlavorService;
import com.bbu.reggie.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author 黄东
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2022-05-11 08:39:37
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




