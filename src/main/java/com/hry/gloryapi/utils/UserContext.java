package com.hry.gloryapi.utils;

import com.hry.gloryapi.model.entity.User;

/**
 * 存储当前登录用户的上下文
 * @author: huangry
 * @create: 2023/10/13
 **/
public class UserContext {

    private static final ThreadLocal<User> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(User user){
        THREAD_LOCAL.set(user);
    }

    public static User getLoginUser(){
        return THREAD_LOCAL.get();
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
