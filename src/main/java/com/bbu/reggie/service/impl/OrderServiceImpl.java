package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.common.BaseContext;
import com.bbu.reggie.common.CustomException;
import com.bbu.reggie.entity.*;
import com.bbu.reggie.mapper.OrderMapper;
import com.bbu.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    @Transactional
    public void saveOrder(Orders order) {
        //获得用户id
        Long userId = BaseContext.getCurrentId();
        //获得使用的地址id
        Long addressId  = order.getAddressBookId();
        //根据用户id查询用户信息
        User user = userService.getById(userId);
        //根据地址id，查询地址信息
        AddressBook address = addressBookService.getById(addressId);
        if(address==null){
            throw new CustomException("地址信息有误不能下单");
        }
        //根据用户id获得购物车信息
        LambdaQueryWrapper<ShoppingCart> cartQueryWrapper = new LambdaQueryWrapper<>();
        cartQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> carts = shoppingCartService.list(cartQueryWrapper);
        if(carts == null || carts.size()==0){
            throw new CustomException("购车为空不能下单！！！");
        }
        //生成订单id,和订单号
        Long orderId = IdWorker.getId();
        //封装订单详情
        AtomicInteger amount = new AtomicInteger();
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(ShoppingCart cart:carts){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(cart.getNumber());
            orderDetail.setDishFlavor(cart.getDishFlavor());
            orderDetail.setDishId(cart.getDishId());
            orderDetail.setSetmealId(cart.getSetmealId());
            orderDetail.setName(cart.getName());
            orderDetail.setImage(cart.getImage());
            orderDetail.setAmount(cart.getAmount());
            amount.addAndGet(cart.getAmount().multiply(new BigDecimal(cart.getNumber())).intValue());
            orderDetails.add(orderDetail);
        }

        //封装订单信息

        order.setId(orderId);
        order.setUserId(userId);
        order.setNumber(String.valueOf(orderId));
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setPayMethod(1);
        order.setStatus(2);
        order.setUserName(user.getName());
        order.setAmount(new BigDecimal(amount.get()));

        order.setPhone(address.getPhone());
        order.setConsignee(address.getConsignee());
        order.setAddress((address.getProvinceName()==null?"":address.getProvinceName())
                +(address.getCityName()==null?"":address.getCityName())
                +(address.getDistrictName()==null?"":address.getDistrictName())
                +(address.getDetail()==null?"":address.getDetail()));
        //保存订单
        this.save(order);
        //保存订单详情
        orderDetailService.saveBatch(orderDetails);
        //根据userId清除购物车信息
        shoppingCartService.remove(cartQueryWrapper);
    }
}
