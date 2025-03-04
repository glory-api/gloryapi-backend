package com.hry.gloryapi.backend.config;

import com.hry.gloryapi.backend.interceptor.LoginInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: huangry
 * @create: 2023/10/13
 **/
@Configuration
@ConditionalOnExpression("'${spring.profiles.active}'=='prod'")
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;
    @Resource
    private LoginPropertise loginPropertise;
    //并不需要拦截的路径集合
//    private static List<String> excludePath = new ArrayList<>();
//    static {
//        excludePath.add("/user/login");
//        excludePath.add("/user/register");
//        excludePath.add("/error");
//        excludePath.add("/swagger-resources");
//        excludePath.add("/swagger-resources/configuration/ui");
//        excludePath.add("/v2/api-docs");
//        excludePath.add("/v2/api-docs-ext");
//        excludePath.add("/doc.html");
//        excludePath.add("/webjars/**");
//        excludePath.add("/favicon.ico");
//    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(loginPropertise.getExcludePaths());
    }
}
