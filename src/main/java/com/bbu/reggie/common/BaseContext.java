package com.bbu.reggie.common;

/**
 * 基于ThreadLocal的工具类，用于获得当前登录用户的id信息
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置id
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取ThreadLocal保存的id
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
