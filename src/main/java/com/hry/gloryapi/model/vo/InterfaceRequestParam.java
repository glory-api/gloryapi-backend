package com.hry.gloryapi.model.vo;

import lombok.Data;

/**
 * 接口信息的请求参数信息
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class InterfaceRequestParam {
    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否必须 0-否 1-是
     */
    private int isRequired;

}
