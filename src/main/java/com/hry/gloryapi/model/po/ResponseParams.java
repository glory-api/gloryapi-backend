package com.hry.gloryapi.model.po;

import lombok.Data;

/**
 * 接口信息的响应参数信息
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class ResponseParams {
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
