package com.hry.gloryapi.backend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hry.gloryapi.backend.model.entity.User;
import com.hry.gloryapi.backend.utils.UserContext;
import com.hry.gloryapi.backend.service.UserService;
import com.hry.gloryapi.backend.constant.UserConstant;
import com.hry.gloryapi.common.enums.ErrorCode;
import com.hry.gloryapi.common.exception.BusinessException;
import com.hry.gloryapi.common.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hry.gloryapi.backend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 登录检查拦截器（登录用户信息存储threadLocal）
 * 所有需要登录才能访问的路径都要被该拦截器拦截
 * @author: huangry
 * @create: 2023/10/13
 **/
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private final static Gson GSON = new Gson();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("登录拦截 访问url:{}",request.getRequestURI());

        try {
            Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
            User currentUser = (User) userObj;
            if (currentUser == null || currentUser.getId() == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            if(UserConstant.BAN_ROLE.equals(currentUser.getUserRole())){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户被禁用，联系管理员查询详情");
            }
            UserContext.set(currentUser);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            response.setContentType("application/json;charset=utf-8");
            if(e instanceof BusinessException){
                BusinessException e1 = (BusinessException) e;
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(objectMapper.writeValueAsString(ResultUtils.error(e1.getCode(),e1.getMessage())));
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
