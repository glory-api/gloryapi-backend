package com.hry.gloryapi.interfaceServer.controller;

import com.hry.gloryapi.common.dto.BaseResponse;
import com.hry.gloryapi.common.enums.ErrorCode;
import com.hry.gloryapi.common.exception.BusinessException;
import com.hry.gloryapi.common.utils.ThrowUtils;
import com.hry.gloryapi.common.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 基本接口服务
 * @author: huangry
 * @create: 2023/10/24
 **/
@RestController
@RequestMapping("/basic")
public class SimpleInterfaceController {
    @GetMapping("/randomNum")
    public BaseResponse<Integer> getRandomNum(int min, int max){
        ThrowUtils.throwIf(max<=min, new BusinessException(ErrorCode.PARAMS_ERROR,"最大值小于等于最小值"));
        SecureRandom random = new SecureRandom("RandomNum".getBytes(StandardCharsets.UTF_8));
        return ResultUtils.success(random.nextInt(max - min)+min);
    }
}
