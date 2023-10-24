package com.hry.gloryapi.backend.controller;

import com.google.gson.Gson;
import com.hry.gloryapi.backend.annotation.AuthCheck;
import com.hry.gloryapi.backend.common.BaseResponse;
import com.hry.gloryapi.backend.common.ErrorCode;
import com.hry.gloryapi.backend.common.PageResponse;
import com.hry.gloryapi.backend.common.ResultUtils;
import com.hry.gloryapi.backend.constant.UserConstant;
import com.hry.gloryapi.backend.exception.BusinessException;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.backend.model.vo.InterfaceInfoVo;
import com.hry.gloryapi.backend.service.InterfaceInfoService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * api平台接口信息管理 接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/interface")
@Slf4j
public class InterfaceInfoController {
    private static final Gson GSON = new Gson();

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 添加信息接口
     * @param interfaceInfoAddRequest
     * @return
     */
    @ApiOperation("添加接口信息")
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@Validated @RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest){
        Long id = interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest);
        return ResultUtils.success(id);
    }

    /**
     * 接口中心列表
     * @param interfaceInfoQueryRequest
     * @return
     */
    @ApiOperation("获取接口信息 分页")
    @PostMapping("/center/page")
    public BaseResponse<PageResponse<InterfaceInfoVo>> listInterfaceInfoVoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest){
        if(interfaceInfoQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //接口中心只展示启用接口
        interfaceInfoQueryRequest.setStatus(2);
        return ResultUtils.success(interfaceInfoService.listInterfaceInfoVoByPage(interfaceInfoQueryRequest));
    }

    /**
     * 接口管理列表 只有管理员可以查询
     * @param interfaceInfoQueryRequest
     * @return
     */
    @ApiOperation("获取接口信息 分页")
    @PostMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageResponse<InterfaceInfoVo>> listInterfaceInfoVoByPageForAdmin(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest){
        if(interfaceInfoQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.listInterfaceInfoVoByPage(interfaceInfoQueryRequest));
    }







}
