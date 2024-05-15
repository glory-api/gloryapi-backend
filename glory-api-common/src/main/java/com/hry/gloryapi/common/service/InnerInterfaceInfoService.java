package com.hry.gloryapi.common.service;

import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.gloryapi.common.model.entity.InterfaceInfo;

/**
* @author lenovo
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-10-13 14:52:34
*/
public interface InnerInterfaceInfoService {
    /**
     * 根据 接口url和接口请求方式 获取接口信息
     * @param url
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfoByUrlAndMethod(String url, String method);
}
