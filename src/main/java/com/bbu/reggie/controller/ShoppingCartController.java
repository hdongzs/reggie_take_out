package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbu.reggie.common.BaseContext;
import com.bbu.reggie.common.R;
import com.bbu.reggie.entity.ShoppingCart;
import com.bbu.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController{
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获得用户的id信息
        Long userId = BaseContext.getCurrentId();
        //根据userId查询用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if(shoppingCart.getDishId()!=null){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart !=null){
            //已存在相同菜品的信息，在原有的基础上进行修改
            cart.setNumber(cart.getNumber()+1);
            shoppingCartService.updateById(cart);
        }else{
            //创建此菜品的购物车
           shoppingCart.setNumber(1);
           shoppingCart.setUserId(userId);
           shoppingCart.setCreateTime(LocalDateTime.now());
           shoppingCartService.save(shoppingCart);
           cart = shoppingCart;
        }
        return R.success(cart);
    }

    /**
     * 减少购物车数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        //获得用户id
        Long userId = BaseContext.getCurrentId();
        //根据用户id查询购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if(shoppingCart.getDishId()!=null){
            //减少的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            //减少的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq(setmealId!=null,ShoppingCart::getSetmealId,setmealId);
        }
        //获得购物车信息
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        int num = cart.getNumber()-1;
        if(num<=0){
            //清除此项购物车
            shoppingCartService.removeById(cart.getId());
        }else{
            //更新购物车数量
            cart.setNumber(num);
            shoppingCartService.updateById(cart);
        }
        return R.success("成功");
    }

    /**
     * 清除购物车
     * @return
     */
    @DeleteMapping("clean")
    public R<String> clean(){
        //获得用户的id
        Long userId = BaseContext.getCurrentId();
        //根据id删除其购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("成功");
    }

    /**
     * 查询所有的购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //获得userId
        Long userId = BaseContext.getCurrentId();
        //根据userId查询购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);
        return R.success(cartList);
    }


}
