package com.hry.gloryapi.backend.utils;

import com.hry.gloryapi.backend.constant.UserConstant;
import com.hry.gloryapi.backend.model.entity.User;
import com.hry.gloryapi.common.enums.ErrorCode;
import com.hry.gloryapi.common.exception.BusinessException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.hry.gloryapi.backend.constant.UserConstant.USER_LOGIN_STATE;

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
        if(Objects.isNull(THREAD_LOCAL.get())){
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes());
            HttpServletRequest request = requestAttributes.getRequest();
            Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
            User currentUser = (User) userObj;
            if (currentUser == null || currentUser.getId() == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            if(UserConstant.BAN_ROLE.equals(currentUser.getUserRole())){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户被禁用，联系管理员查询详情");
            }

            UserContext.set(currentUser);
        }
        return THREAD_LOCAL.get();
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
