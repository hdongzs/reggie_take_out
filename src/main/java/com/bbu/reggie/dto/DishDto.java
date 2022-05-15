package com.bbu.reggie.dto;

import com.bbu.reggie.entity.Dish;
import com.bbu.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors;

    private String categoryName;

    private Integer copies;
}
