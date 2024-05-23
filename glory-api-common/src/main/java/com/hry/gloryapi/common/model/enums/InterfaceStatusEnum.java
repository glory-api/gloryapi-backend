package com.hry.gloryapi.common.model.enums;

import lombok.Data;

/**
 * 接口状态 枚举类
 * @author: huangry
 * @create: 2024/1/11
 **/
public enum InterfaceStatusEnum {
    /**
     * 未启用
     */
    OFF("0","未启用"),
    /**
     * 启用
     */
    ON("1","启用");

    private String code;
    private String desc;

    InterfaceStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isOff(String code){
        return OFF.code.equals(code);
    }

    public static boolean isOn(String code){
        return ON.code.equals(code);
    }

}
