package com.hry.gloryapi.common.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserVo implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 开发者凭证
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 用户积分
     */
    private Long integral;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}