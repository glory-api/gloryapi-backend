package com.hry.gloryapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 接口信息
 * @TableName interface_info
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 域名
     */
    private String domain;

    /**
     * 网关
     */
    private String gateway;

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
     * 响应示例
     */
    private String responseExample;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private String status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 积分
     */
    private Long integral;

    /**
     * 创建人
     */
    private String userid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}