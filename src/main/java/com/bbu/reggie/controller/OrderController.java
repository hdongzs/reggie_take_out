package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbu.reggie.common.BaseContext;
import com.bbu.reggie.common.R;

import com.bbu.reggie.entity.Orders;
import com.bbu.reggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 将提交的订单信息封装，并保存至数据库订单表和订单详情表
     * @param order 前端传来的订单信息，封装了addressBookId,payMethod,remark
     * @return
     */
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders order){
        orderService.saveOrder(order);
        return R.success("成功");
    }

    /**
     * 分页查询当前用户的订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(Integer page,Integer pageSize){
        //获得用户的id
        Long userId = BaseContext.getCurrentId();
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId)
                .orderByDesc(Orders::getCheckoutTime);
        //获得订单信息
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @GetMapping("page")
    public R<Page<Orders>> orderPage(Integer page, Integer pageSize, String number,
                                     String beginTime, String endTime){

        Page<Orders> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //构造查询条件
        queryWrapper.like(number != null && !"".equals(number), Orders::getNumber, number)
                .ge(beginTime != null, Orders::getCheckoutTime, beginTime)
                .le(endTime != null, Orders::getCheckoutTime, endTime)
                .orderByDesc(Orders::getCheckoutTime);

        //获得订单信息
        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> updateStatus(@RequestBody Orders order){
        //根据订单id修改订单的状态
//        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
//        updateWrapper.set(Orders::getStatus,order.getStatus());
        orderService.updateById(order);
        return R.success("成功");
    }

}
