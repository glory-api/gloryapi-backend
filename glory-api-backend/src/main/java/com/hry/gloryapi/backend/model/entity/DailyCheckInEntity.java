package com.hry.gloryapi.backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 每日签到表
 * @TableName daily_check_in
 */
@Data
@TableName(value ="daily_check_in")
public class DailyCheckInEntity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 签到人
     */
    private String userId;

    /**
     * 描述
     */
    private String description;

    /**
     * 签到增加积分个数
     */
    private Long addPoints;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}