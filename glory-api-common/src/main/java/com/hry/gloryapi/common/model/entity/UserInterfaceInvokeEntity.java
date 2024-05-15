package com.hry.gloryapi.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户接口调用表
 * @TableName user_interface_invoke
 */
@TableName(value ="user_interface_invoke")
@Data
public class UserInterfaceInvokeEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 调用人id
     */
    private String userId;

    /**
     * 接口id
     */
    private String interfaceId;

    /**
     * 总调用次数
     */
    private Long totalInvokes;

    /**
     * 调用状态（0- 禁用 1-正常）
     */
    private String status = "1";

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}