package com.hry.gloryapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hry.glory.common.enums.ErrorCode;
import com.hry.gloryapi.backend.mapper.UserInterfaceInvokeEntityMapper;
import com.hry.gloryapi.backend.service.UserInterfaceInvokeService;
import com.hry.gloryapi.backend.service.UserService;
import com.hry.gloryapi.common.exception.ApiBusinessException;
import com.hry.gloryapi.common.model.entity.UserInterfaceInvokeEntity;
import com.hry.gloryapi.common.service.InnerUserInterfaceInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author lenovo
* @description 针对表【user_interface_invoke(用户接口调用表)】的数据库操作Service实现
* @createDate 2024-05-08 16:49:40
*/
@DubboService
public class InnerUserInterfaceInvokeServiceImpl extends ServiceImpl<UserInterfaceInvokeEntityMapper, UserInterfaceInvokeEntity> implements InnerUserInterfaceInvokeService {

    private UserService userService;

    @Resource
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @Override
    public boolean afterInvoke(String userId, String interfaceId, Integer reduceScore) {
        //获取用户调用接口记录信息
        UserInterfaceInvokeEntity entity = getOneByUserIdAndInterfaceId(userId,interfaceId);
        boolean reduceResult = true;
        if(entity == null){
            //从未调用，新建记录
            entity = new UserInterfaceInvokeEntity();
            entity.setInterfaceId(interfaceId);
            entity.setUserId(userId);
            entity.setTotalInvokes(1L);
            reduceResult &= this.save(entity);
        }else {
            //调用次数+1
            LambdaUpdateWrapper<UserInterfaceInvokeEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserInterfaceInvokeEntity::getId, entity.getId())
                .setSql("totalInvokes = totalInvokes + 1");
            reduceResult &=this.update(updateWrapper);
        }
        //用户积分-1
        reduceResult &= userService.reduceIntegral(userId, reduceScore);
        if(!reduceResult){
            throw new ApiBusinessException(ErrorCode.OPERATION_ERROR,"调用失败");
        }
        return reduceResult;
    }

    @Override
    public UserInterfaceInvokeEntity getOneByUserIdAndInterfaceId(String userId, String interfaceId) {
        LambdaQueryWrapper<UserInterfaceInvokeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInterfaceInvokeEntity::getInterfaceId,interfaceId)
            .eq(UserInterfaceInvokeEntity::getUserId, userId);
        return this.getOne(queryWrapper);
    }

}




