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
public class InterfaceInfoConstant {
    public static List<String> sortFields;
    static {
        Field[] interfaceInfoFields = InterfaceInfo.class.getDeclaredFields();
        sortFields = Arrays.stream(interfaceInfoFields).map(field -> {
            field.setAccessible(true);
            return field.getName();
        }).collect(Collectors.toList());
    }
}
