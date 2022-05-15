package com.bbu.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.bbu.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {

    public void saveOrder(Orders order);
}
