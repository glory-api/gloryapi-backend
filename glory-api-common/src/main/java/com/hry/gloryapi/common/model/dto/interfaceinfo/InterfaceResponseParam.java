package com.hry.gloryapi.common.model.dto.interfaceinfo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 接口信息的响应参数信息
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class InterfaceResponseParam implements Serializable {
    /**
     * 参数名称
     */
    @NotBlank(message = "响应参数名不能为空")
    private String name;

    /**
     * 参数类型
     */
    @NotBlank(message = "响应参数类型不能为空")
    private String type;

    /**
     * 描述
     */
    @NotBlank(message = "响应参数类型不能为空")
    private String description;

    private static final long serialVersionUID = 1L;

}
