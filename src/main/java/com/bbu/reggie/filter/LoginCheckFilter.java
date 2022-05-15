package com.bbu.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.bbu.reggie.common.BaseContext;
import com.bbu.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName="loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的路径
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String []urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //判断是否需要处理本次请求
        boolean check = check(urls,requestURI);
        //如果不需要处理，则直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态,如果以登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        //判断客户端是否登录
        if(request.getSession().getAttribute("user")!=null){
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录，则返回json信息
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return match;
            }
        }
        return false;
    }
}
