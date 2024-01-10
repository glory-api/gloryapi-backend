package com.hry.gloryapi.interfaceServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 开放接口服务启动类
 * @author: huangry
 * @create: 2023/10/24
 **/
@SpringBootApplication(scanBasePackages = {"com.hry.gloryapi"})
public class InterfaceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterfaceServerApplication.class,args);
    }
}
