package com.hry.gloryapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hry.gloryapi.backend.service.UserService;
import com.hry.gloryapi.common.model.entity.User;
import com.hry.gloryapi.common.model.vo.UserVo;
import com.hry.gloryapi.common.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author: huangry
 * @create: 2024/5/15
 **/
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserService userService;

    @Override
    public UserVo getOneByAccessKey(String ak) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccessKey, ak);
        User user = userService.getOne(queryWrapper);
        return userService.getUserVO(user);
    }
}
