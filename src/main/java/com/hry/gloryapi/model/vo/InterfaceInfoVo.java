package com.hry.gloryapi.model.vo;



import java.io.Serializable;
import java.util.Date;

/**
 * @author: huangry
 * @create: 2023/10/13
 **/
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
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应参数
     */
    private String responseParams;

    /**
     * 响应头
     */
    private String responseHeader;

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
    private Date createTime;

}
