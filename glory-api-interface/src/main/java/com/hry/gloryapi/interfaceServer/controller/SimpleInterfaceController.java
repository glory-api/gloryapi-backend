package com.hry.gloryapi.interfaceServer.controller;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.glory.common.utils.ResultUtils;
import com.hry.glory.common.utils.ThrowUtils;
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
@RequestMapping("/api/interface/basic")
public class SimpleInterfaceController {
    /**
     * 随机数生成
     * @param min
     * @param max
     * @return
     */
    @GetMapping("/randomNum")
    public BaseResponse<Long> getRandomNum(long min, long max){
        ThrowUtils.throwIf(max<=min, new BusinessException(ErrorCode.PARAMS_ERROR,"最大值小于等于最小值"));
        SecureRandom random = new SecureRandom("RandomNum".getBytes(StandardCharsets.UTF_8));
        return ResultUtils.success(random.nextLong(max - min)+min);
    }
}
