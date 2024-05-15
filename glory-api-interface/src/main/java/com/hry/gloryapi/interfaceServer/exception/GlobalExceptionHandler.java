package com.hry.gloryapi.interfaceServer.exception;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.exception.ForbiddenException;
import com.hry.glory.common.exception.UnauthorizedException;
import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.glory.common.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<Boolean> unauthorizedExceptionHandler(BusinessException e) {
        log.error("UnauthorizedException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<Boolean> forbiddenExceptionHandler(BusinessException e) {
        log.error("ForbiddenException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Boolean> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({BindException.class,})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Boolean> businessExceptionHandler(BindException e) {
        log.error("MethodArgumentNotValidException", e);
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        if (bindingResult.hasErrors()) {

            List<ObjectError> errors = bindingResult.getAllErrors();
            if (errors != null) {
                errors.forEach(p -> {
                    FieldError fieldError = (FieldError) p;
                    log.error("Bad Request Parameters: dto entity [{}],field [{}],message [{}]",fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
                    stringBuilder.append("请求参数错误："+"["+fieldError.getField()+"]"+":"+fieldError.getDefaultMessage()+";");
                });
            }
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), stringBuilder.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Boolean> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage()+"系统错误，请查看系统运行日志获得进一步的信息。");
    }
}
