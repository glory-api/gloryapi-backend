package com.hry.gloryapi.common.exception;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;

/**
 * 自定义异常类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ApiBusinessException extends BusinessException {

    public ApiBusinessException(){}

    public ApiBusinessException(int code, String message) {
        super(code, message);
    }

    public ApiBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ApiBusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public int getCode() {
        return super.getCode();
    }
}
