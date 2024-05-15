package com.hry.gloryapi.backend.service.impl.inner;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.glory.common.utils.ResultUtils;
import com.hry.gloryapi.backend.service.InterfaceInfoService;
import com.hry.gloryapi.backend.utils.SpringContextUtils;
import com.hry.gloryapi.common.exception.ApiBusinessException;
import com.hry.gloryapi.common.model.entity.InterfaceInfo;
import com.hry.gloryapi.common.service.InnerInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

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
            throw new ApiBusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceInfo::getUrl,url).eq(InterfaceInfo::getMethod,method);
        InterfaceInfo interfaceInfo = interfaceInfoService.getOne(wrapper);
        return interfaceInfo;
    }

}
