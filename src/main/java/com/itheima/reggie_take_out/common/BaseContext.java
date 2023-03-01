package com.itheima.reggie_take_out.common;

/**
 * @author 陶月松
 * @create 2023-02-26 17:25
 *
 * 基于THreadLocal封装工具类：用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    //设置值
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    //获取值
    public static Long getCurrenId(){
        return threadLocal.get();
    }

}
