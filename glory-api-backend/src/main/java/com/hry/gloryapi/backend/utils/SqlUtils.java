package com.hry.gloryapi.backend.utils;

import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL 工具
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class SqlUtils {
    private SqlUtils() {
    }

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return true 合法  false 不合法或为null
     */
    public static boolean validSortField(String sortField, Class<?> clazz) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }

//        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
        //使用mybatisPlus 提供的校验注入工具
        if(SqlInjectionUtils.check(sortField)){
            return false;
        }
        //查询排序字段是否存在
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldsName = Arrays.stream(fields).map(field -> {
            field.setAccessible(true);
            return field.getName();
        }).collect(Collectors.toList());

        return fieldsName.contains(sortField);
    }
}
