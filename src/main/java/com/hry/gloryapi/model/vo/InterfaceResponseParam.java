package com.hry.gloryapi.model.vo;

import lombok.Data;

/**
 * 接口信息的响应参数信息
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class InterfaceResponseParam {
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

}
