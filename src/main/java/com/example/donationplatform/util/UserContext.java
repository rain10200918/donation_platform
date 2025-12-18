package com.example.donationplatform.util;

import com.example.donationplatform.entity.Users;

/**
 * 用户上下文工具类
 * 用于在多线程环境中存储和获取当前用户信息
 * 使用ThreadLocal确保每个线程都有独立的用户数据副本
 */
public class UserContext {
    /**
     * 线程本地变量，用于存储当前线程的用户信息
     * 每个线程都有自己独立的用户对象实例
     */
    public static final ThreadLocal<Users> USER_THREAD_LOCAL=new ThreadLocal<>();

    /**
     * 设置当前线程的用户信息
     * @param user 用户对象，不能为空
     */
    public static void setUser(Users user){
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前线程的用户信息
     * @return 返回当前线程存储的用户对象，如果未设置则返回null
     */
    public static Users getUser(){
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 清除当前线程的用户信息
     * 防止内存泄漏，使用完毕后必须调用此方法清理ThreadLocal
     */
    public static void removeUser(){
        USER_THREAD_LOCAL.remove();
    }
}
