package com.hry.gloryapi.backend.model.dto.interfaceinfo;

import com.hry.gloryapi.backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name = "";

    /**
     * 描述
     */
    private String description = "";

    /**
     * 接口地址
     */
    private String url = "";

    /**
     * 接口状态（0-关闭，1开启）
     */
    private String status = "";

    /**
     * 请求类型
     */
    private String method = "";

}
