package com.hry.gloryapi.backend.aop;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.glory.common.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * RPC内部服务调用返回统一处理
 *
 * @author: huangry
 * @create: 2024/5/7
 **/
//@Aspect
//@Component
@Order(2)
@Slf4j
public class InnerServiceResponseAop {
    /**
     * 所有对内服务类作为切入点
     */
    @Pointcut(value = "execution(public * com.hry.gloryapi.backend.service.impl.inner.*.*(..))")
    public void innerServicePointcut() {
    }

    /**
     * 存在事务注解的切入点
     */
    @Pointcut(value = "@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalPointcut() {
    }

    /**
     * 内部服务，不带事务
     * @param point
     * @return
     */
    @Around("innerServicePointcut() && !transactionalPointcut()")
    public Object doAround(ProceedingJoinPoint point) {
        // 获取请求参数
        Object[] args = point.getArgs();
        // 执行原方法
        try {
            return point.proceed();
        } catch (BusinessException e) {
            //业务异常
            processException(point,args);
            e.printStackTrace();
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Throwable e) {
            processException(point,args);
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 内部服务，又带事务
     * @param point
     * @return
     */
    @Around("innerServicePointcut() && transactionalPointcut()")
    public Object doTransactionAround(ProceedingJoinPoint point) throws Throwable {
        // 获取请求参数
        Object[] args = point.getArgs();
        // 执行原方法
        try {
            return point.proceed();
        } catch (BusinessException e) {
            //业务异常，包装为RuntimeException抛出，保证事务正常，保证Dubbo不额外处理异常。
            processException(point,args);
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void processException(ProceedingJoinPoint point, Object[] args){
        String inputParam = "";

        if(args != null && args.length > 0){
            StringBuilder sb = new StringBuilder();
            for (Object arg : args) {
                sb.append(",");
                sb.append(arg);
            }
            inputParam = sb.toString().substring(1);
        }
        log.error("\n方法-->[{}.{}] 调用异常 \n参数-->[{}]",point.getSignature().getDeclaringTypeName(),point.getSignature().getName(),inputParam);
    }
}
