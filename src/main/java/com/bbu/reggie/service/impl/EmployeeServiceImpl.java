package com.bbu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbu.reggie.entity.Employee;
import com.bbu.reggie.service.EmployeeService;
import com.bbu.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author 黄东
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-04-29 22:16:54
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




