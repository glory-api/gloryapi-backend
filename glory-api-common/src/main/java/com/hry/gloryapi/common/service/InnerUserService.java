package com.hry.gloryapi.common.service;

import com.hry.gloryapi.common.model.vo.UserVo;

/**
 * @author: huangry
 * @create: 2024/5/15
 **/
public interface InnerUserService {
    /**
     * 根据AccessKey 获取 用户
     * @param ak
     * @return
     */
    UserVo getOneByAccessKey(String ak);
}
