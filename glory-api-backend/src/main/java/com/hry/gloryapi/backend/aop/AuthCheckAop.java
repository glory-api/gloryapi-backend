package com.hry.gloryapi.backend.aop;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.gloryapi.backend.annotation.AuthCheck;
import com.hry.gloryapi.backend.service.UserService;
import com.hry.gloryapi.common.model.entity.User;
import com.hry.gloryapi.common.model.enums.UserRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Slf4j
@Aspect
@Component
@Order(1)//切面类顺序 越小越先执行
public class AuthCheckAop {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        log.info("权限校验");
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            //判断权限注解中是否写了非法权限（例如写了不存在的权限）
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                log.error("权限定义不合法 无[{}]权限定义",mustRole);
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
//            // 如果被封号，直接拒绝
//            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//            }
            // 必须有管理员权限
//            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
//                if (!mustRole.equals(userRole)) {
//                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//                }
//            }
            //当前登录用户是否符合权限
            if (!mustRole.equals(userRole)) {
                log.error("用户无[{}]权限",mustRole);
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 通过权限校验，放行
        log.info("权限校验通过：userRole[{}]", loginUser.getUserRole());
        return joinPoint.proceed();
    }
}

