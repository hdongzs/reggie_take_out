package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbu.reggie.common.R;
import com.bbu.reggie.entity.Employee;
import com.bbu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //1、将页面提交的密码进行md5进行加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据用户名username查询数据库的数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败的结果
        if(emp == null){
            return R.error("用户名不存在");
        }

        //4、比对密码，不一致则返回密码错误
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5、判断账号的状态是否为1，不为1就是被锁定
        if(emp.getStatus() == 0){
            return R.error("账号被锁定");
        }
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 员工添加，修改信息功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> saveEmployee(HttpServletRequest request,@RequestBody Employee employee){
//        HttpSession session = request.getSession();
//        Long empId = (Long) session.getAttribute("employee");
        //封装员工的信息
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.save(employee);
        return R.success("添加员工成功");
    }

    /**
     * 员工信息展示功能，分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> getEmployeeByPage(Integer page,Integer pageSize,String name){
        //跟据条件查询员工信息，并分页
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> updateStatus(HttpServletRequest request ,@RequestBody Employee employee){
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return R.success("状态转换成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable("id") Long id){
        Employee emp = employeeService.getById(id);
        if(emp !=null){
            return R.success(emp);
        }
        return R.error("没有此员工的信息");
    }
}
