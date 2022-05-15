package com.bbu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbu.reggie.common.R;
import com.bbu.reggie.entity.User;
import com.bbu.reggie.service.UserService;
import com.bbu.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 模拟生成验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        //获得验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code:{}",code);
        //将验证码保存至session域
        session.setAttribute(phone,code);
        return R.success("验证码生成成功");
    }

    /**
     * 登录验证
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map, HttpSession session){
        String code1 = map.get("code");
        String phone = map.get("phone");
        //验证验证码是否正确
        String code2 =(String) session.getAttribute(phone);
        if(!code1.equals(code2)){
            //验证码错误
            return R.error("验证码错误！！！！");
        }
        //根据手机号码查询用户是否已经存在，存在则返回，不存在则创建对象再返回
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if(user == null){
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());
        return R.success(user);
    }


}
