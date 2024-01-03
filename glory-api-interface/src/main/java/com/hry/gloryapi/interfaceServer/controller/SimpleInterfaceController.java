package com.hry.gloryapi.interfaceServer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 简单接口服务
 * @author: huangry
 * @create: 2023/10/24
 **/
@RestController
@RequestMapping("/simple")
public class SimpleInterfaceController {
    @GetMapping("/randomNum")
    public Integer getRandomNum(int pre, int suf){
        Random random = new Random();
        if(suf > pre){
            return random.nextInt(suf - pre)+pre;
        }
        return null;
    }
}
