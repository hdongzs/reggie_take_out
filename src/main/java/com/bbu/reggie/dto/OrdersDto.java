package com.bbu.reggie.dto;

import com.bbu.reggie.entity.Orders;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrdersDto extends Orders {

    private LocalDateTime beginTime;

    private LocalDateTime endTime;
}
