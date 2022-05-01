package com.bbu.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("category")
public class Category {
    private Long id;
    private Integer type;
    private String name;
    private Integer sort;

    @TableField(value="create_time",fill= FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value="update_time",fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(value="create_user",fill=FieldFill.INSERT)
    private Long createUser;
    @TableField(value="update_user",fill=FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
