package com.itheima.reggie.common;

//线程工具类, 基于ThreadLocal
public class BaseContext {
    //初始化ThreadLocal对象
    private static final ThreadLocal<Long> threadLocal= new ThreadLocal<>();

    /**
     * 存储id到线程局部变量
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 返回线程存储的局部变量
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
