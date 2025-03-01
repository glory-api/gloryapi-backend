package com.hry.gloryapi.backend.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.gloryapi.backend.constant.CommonConstant;
import com.hry.gloryapi.backend.mapper.UserMapper;
import com.hry.gloryapi.backend.model.dto.user.UserQueryRequest;
import com.hry.gloryapi.backend.model.entity.DailyCheckInEntity;
import com.hry.gloryapi.backend.model.vo.LoginUserVo;
import com.hry.gloryapi.backend.service.DailyCheckInService;
import com.hry.gloryapi.backend.utils.SpringContextUtils;
import com.hry.gloryapi.backend.utils.UserContext;
import com.hry.gloryapi.common.model.vo.UserVo;
import com.hry.gloryapi.backend.service.UserService;
import com.hry.gloryapi.backend.utils.SqlUtils;
import com.hry.gloryapi.common.model.entity.User;
import com.hry.gloryapi.common.model.enums.UserRoleEnum;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hry.gloryapi.backend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private DailyCheckInService dailyCheckInService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "glory";

    @Override
    public String userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //加锁防止并发注册下产生的线程安全问题
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            String accessKey = DigestUtils.md5DigestAsHex((SALT + userAccount + RandomUtil.randomNumbers(5) + LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + userAccount + RandomUtil.randomNumbers(6) + LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userAccount);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        //用户被禁用
        if (isBan(user)) {
            log.info("user login failed, userAccount is Disabled");
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户被禁用");
        }
        // 3. 记录用户的登录态

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public LoginUserVo userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        String userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        String userId = currentUser.getId();
        return this.getById(userId);
    }


    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean isBan(User user) {
        return user != null && UserRoleEnum.BAN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVo getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVo loginUserVO = new LoginUserVo();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVo getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVo userVO = new UserVo();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVo> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public boolean increaseIntegral(String userId, Long increaseScore) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        wrapper.setSql("integral = integral + {0}",increaseScore);
        return update(wrapper);
    }

    @Override
    public boolean reduceIntegral(String userId, Long reduceScore) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        wrapper.setSql("integral = integral - {0}",reduceScore);
        return update(wrapper);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField, User.class), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
            sortField);
        return queryWrapper;
    }

    @Override
    public String sign() {
        //获取当前登录用户
//        User loginUser = UserContext.getLoginUser();
//        synchronizedSign();
//        redisSign();
        redissonSign();
        //计算当前实际按
        //根据 user 账户和当天日期，组合形成key，
//        redisTemplate.opsForValue().setIfAbsent(,"1")

        return null;
    }

    @Transactional
    @Override
    public Boolean saveCheckInLogAndIncreaseIntegral(String userId){
        DailyCheckInEntity entity = new DailyCheckInEntity();
        entity.setUserId(userId);
        Long integral = 10L;
        entity.setAddPoints(integral);
        dailyCheckInService.save(entity);
        increaseIntegral(userId,integral);
        return true;
    }

    //同步锁
    public String synchronizedSign(){
        //获取当前登录用户
//        User loginUser = UserContext.getLoginUser();
        User loginUser = new User();
        loginUser.setId("1716702178614874113");
        loginUser.setUserAccount("admin");

        synchronized (loginUser.getUserAccount().intern()){
            //查询数据库当前用户是否存在签到记录
            LocalDateTime beginTime = LocalDate.now().atStartOfDay();
            LocalDateTime endTime = LocalDate.now().plusDays(1).atStartOfDay();
            List<DailyCheckInEntity> dailyCheckInDatas = dailyCheckInService.listByUserIdAndTime(loginUser.getId(), beginTime, endTime);
            if(dailyCheckInDatas.isEmpty()){
                //未签到，添加签到记录，增加用户积分
                SpringContextUtils.getBean(UserService.class).saveCheckInLogAndIncreaseIntegral(loginUser.getId());
                return "success";
            }else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复签到");
            }

        }
    }


    //redis
    public String redisSign(){
        //获取当前登录用户
//        User loginUser = UserContext.getLoginUser();
        User loginUser = new User();
        loginUser.setId("1716702178614874113");
        loginUser.setUserAccount("admin");

        //用户唯一标识+当前日期 做key
        String key = loginUser.getId()+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Duration exp = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
        Boolean ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(key, "1" ,exp);
        if(ifAbsent){
            //今天还未签到
            SpringContextUtils.getBean(UserService.class).saveCheckInLogAndIncreaseIntegral(loginUser.getId());
            return "success";
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复签到");
    }

    //redisson
    public String redissonSign(){
//        User loginUser = UserContext.getLoginUser();
        User loginUser = new User();
        loginUser.setId("1716702178614874113");
        loginUser.setUserAccount("admin");

        //用户唯一标识 作为锁标识
        String lockKey = loginUser.getId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if(lock.tryLock()){
                //查询数据库当前用户是否存在签到记录
                LocalDateTime beginTime = LocalDate.now().atStartOfDay();
                LocalDateTime endTime = LocalDate.now().plusDays(1).atStartOfDay();
                List<DailyCheckInEntity> dailyCheckInDatas = dailyCheckInService.listByUserIdAndTime(loginUser.getId(), beginTime, endTime);
                if(dailyCheckInDatas.isEmpty()){
                    //未签到，添加签到记录，增加用户积分
                    SpringContextUtils.getBean(UserService.class).saveCheckInLogAndIncreaseIntegral(loginUser.getId());
                    return "success";
                }else {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复签到");
                }
            }
            log.error("获取锁失败");
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"签到失败");
        } catch (Exception e) {
            if(!(e instanceof BusinessException)){
                log.error("签到失败",e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"签到失败");
            }
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.error("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
