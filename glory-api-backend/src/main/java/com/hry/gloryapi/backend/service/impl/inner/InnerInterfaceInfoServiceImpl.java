package com.hry.gloryapi.backend.service.impl.inner;


import com.google.gson.Gson;
import com.hry.gloryapi.backend.service.InterfaceInfoService;
import com.hry.gloryapi.common.model.entity.InterfaceInfo;
import com.hry.gloryapi.common.service.InnerInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author huangry
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-10-13 14:52:34
 */
@DubboService
@Slf4j
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    private static final Gson GSON = new Gson();


    @Override
    public InterfaceInfo getInterfaceInfoByUrlAndMethod(String url, String method) {
        //参数校验
        if(StringUtils.isAnyBlank(url,method)){

        }
        System.out.println(url+method);
        return null;
    }
}
