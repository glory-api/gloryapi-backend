package com.hry.gloryapi.backend.constant;

import com.hry.gloryapi.backend.model.entity.InterfaceInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息 相关 常量
 * @author: huangry
 * @create: 2023/10/20
 **/
public interface InterfaceInfoConstant {
    /**
     * 接口状态：开启
     */
    Integer INTERFACE_STATUS_ON = 1;

    /**
     * 接口状态：关闭
     */
    Integer INTERFACE_STATUS_OFF = 0;
}
