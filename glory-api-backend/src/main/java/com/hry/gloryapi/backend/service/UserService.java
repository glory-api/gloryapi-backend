package com.hry.gloryapi.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hry.gloryapi.backend.model.dto.user.UserQueryRequest;
import com.hry.gloryapi.backend.model.vo.LoginUserVo;
import com.hry.gloryapi.common.model.vo.UserVo;
import com.hry.gloryapi.common.model.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    String userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 当前用户是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 查询某用户是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 是否被封禁用户
     *
     * @param user
     * @return
     */
    boolean isBan(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVo getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVo getUserVO(User user);

    /**
     * 获取脱敏的用户信息列表
     *
     * @param userList
     * @return
     */
    List<UserVo> getUserVO(List<User> userList);

    /**
     * 减少用户积分
     * @param userId
     * @param increaseScore
     * @return
     */
    boolean increaseIntegral(String userId, Integer increaseScore);

    /**
     * 减少用户积分
     * @param userId
     * @param reduceScore
     * @return
     */
    boolean reduceIntegral(String userId, Integer reduceScore);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
