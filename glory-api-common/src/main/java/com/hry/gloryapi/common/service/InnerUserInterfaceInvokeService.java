package com.hry.gloryapi.common.service;

import com.hry.gloryapi.common.model.entity.UserInterfaceInvokeEntity;

/**
* @author lenovo
* @description 针对表【user_interface_invoke(用户接口调用表)】的数据库操作Service
* @createDate 2024-05-08 16:49:40
*/
public interface InnerUserInterfaceInvokeService{
    /**
     * 接口调用后信息维护
     * @param userId
     * @param interfaceId
     * @return
     */
    boolean afterInvoke(String userId, String interfaceId, Integer reduceScore);

    /**
     * 根据用户id和接口id 获取用户调用接口信息
     * @param userId
     * @param interfaceId
     * @return
     */
    UserInterfaceInvokeEntity getOneByUserIdAndInterfaceId(String userId, String interfaceId);
}
