package com.hry.gloryapi.common.model.enums;

/**
 * 接口状态 枚举类
 * @author: huangry
 * @create: 2024/1/11
 **/
public enum UserInterfaceInvokeEnum {
    /**
     * 未启用
     */
    OFF("0","禁用"),
    /**
     * 启用
     */
    ON("1","启用");

    private String code;
    private String desc;

    UserInterfaceInvokeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
