package com.hry.gloryapi.interfaceServer.exception;

import com.hry.gloryapi.common.enums.ErrorCode;

/**
 * @author: huangry
 * @create: 2024/1/3
 **/
public class InterfaceException extends RuntimeException{
    private int code;

    public InterfaceException(int code,String message) {
        super(message);
        this.code = code;
    }

    public InterfaceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public InterfaceException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
