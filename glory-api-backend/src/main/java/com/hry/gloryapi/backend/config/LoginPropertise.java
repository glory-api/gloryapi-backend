package com.hry.gloryapi.backend.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 登录拦截器 配置属性
 * @author: huangry
 * @create: 2024/1/3
 **/
@Data
@Component
@ConfigurationProperties(prefix = "login")
public class LoginPropertise {
    private List<String> excludePaths;
}
