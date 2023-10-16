package com.hry.gloryapi.interceptor;

import com.google.gson.Gson;
import com.hry.gloryapi.common.ErrorCode;
import com.hry.gloryapi.common.ResultUtils;
import com.hry.gloryapi.exception.BusinessException;
import com.hry.gloryapi.model.entity.User;
import com.hry.gloryapi.service.UserService;
import com.hry.gloryapi.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hry.gloryapi.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 登录检查拦截器（登录用户信息存储threadLocal）
 * @author: huangry
 * @create: 2023/10/13
 **/
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    private final static Gson GSON = new Gson();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.error(request.getRequestURI());

        try {
            Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
            User currentUser = (User) userObj;
            if (currentUser == null || currentUser.getId() == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            UserContext.set(currentUser);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            response.setContentType("application/json;charset=utf-8");
            if(e instanceof BusinessException){
                BusinessException e1 = (BusinessException) e;
                response.getWriter().write(GSON.toJson(ResultUtils.error(e1.getCode(), e.getMessage())));
            }else {
                response.getWriter().write(GSON.toJson(ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误")));
            }
            return false;
        }
//        finally {
//            UserContext.remove();
//        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
