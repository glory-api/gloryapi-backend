package com.hry.gloryapi.common.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.hry.gloryapi.common.model.dto.interfaceinfo.InterfaceRequestParam;
import com.hry.gloryapi.common.model.dto.interfaceinfo.InterfaceResponseParam;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class InterfaceInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private List<InterfaceRequestParam> requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应参数
     */
    private List<InterfaceResponseParam> responseParams;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 响应示例
     */
    private String responseExample;

    /**
     * 接口状态（0-未审批，1-关闭，2-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
