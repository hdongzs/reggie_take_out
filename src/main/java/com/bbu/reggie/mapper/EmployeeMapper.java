package com.bbu.reggie.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bbu.reggie.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 黄东
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2022-04-29 22:16:54
* @Entity com.bbu.reggie.entity.Employee
*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}




