package com.hry.gloryapi.backend.controller;

import com.google.gson.Gson;
import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.glory.common.utils.ResultUtils;
import com.hry.gloryapi.backend.annotation.AuthCheck;
import com.hry.gloryapi.backend.constant.UserConstant;
import com.hry.gloryapi.backend.service.InterfaceInfoService;
import com.hry.gloryapi.common.common.IdRequest;
import com.hry.gloryapi.common.common.PageResponse;
import com.hry.gloryapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.hry.gloryapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.hry.gloryapi.common.model.dto.interfaceinfo.TestInvokeRequest;
import com.hry.gloryapi.common.model.enums.InterfaceStatusEnum;
import com.hry.gloryapi.common.model.vo.InterfaceInfoVo;
import com.hry.gloryapi.common.service.InnerUserInterfaceInvokeService;
import com.hry.gloryapisdk.client.BasicClient;
import com.hry.gloryapisdk.client.build.GeneralClientBuild;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * api平台接口信息管理 接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/api/interface")
@Slf4j
public class InterfaceInfoController {
    private static final Gson GSON = new Gson();

    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private InnerUserInterfaceInvokeService innerUserInterfaceInvokeService;

    /**
     * 添加信息接口
     * @param interfaceInfoAddRequest
     * @return
     */
    @ApiOperation("添加接口信息")
    @PostMapping("/admin/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> addInterfaceInfo(@Validated @RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest){
        String id = interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest);
        return ResultUtils.success(id);
    }

    /**
     * 接口中心列表
     * @param interfaceInfoQueryRequest
     * @return
     */
    @ApiOperation("获取接口信息 分页")
    @GetMapping("/center/page")
    public BaseResponse<PageResponse<InterfaceInfoVo>> listInterfaceInfoVoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest){
        if(interfaceInfoQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //接口中心只展示启用接口
        interfaceInfoQueryRequest.setStatus(InterfaceStatusEnum.ON.getCode());
        return ResultUtils.success(interfaceInfoService.listInterfaceInfoVoByPage(interfaceInfoQueryRequest));
    }

    /**
     * 管理页接口管理列表 管理员查询
     * @param interfaceInfoQueryRequest
     * @return
     */
    @ApiOperation("获取管理页接口信息 分页")
    @PostMapping("/admin/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PageResponse<InterfaceInfoVo>> listInterfaceInfoVoByPageForAdmin(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest){
        if(interfaceInfoQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.listInterfaceInfoVoByPage(interfaceInfoQueryRequest));
    }

    /**
     * 上线接口
     * @param idRequest
     * @return
     */
    @ApiOperation("上线接口")
    @PostMapping("/admin/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> onlineInterface(@Validated @RequestBody IdRequest idRequest){
        interfaceInfoService.updateStatus(idRequest);
        return ResultUtils.success("ok");
    }

    /**
     * 下线接口
     * @param idRequest
     * @return
     */
    @ApiOperation("下线接口")
    @PostMapping("/admin/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> offlineInterface(@Validated @RequestBody IdRequest idRequest){
        interfaceInfoService.updateStatus(idRequest);
        return ResultUtils.success("ok");
    }

    /**
     * 修改接口信息
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @ApiOperation("修改接口信息")
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> updateInterfaceInfo(@Validated @RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest){
        interfaceInfoService.updateInterfaceInfo(interfaceInfoUpdateRequest);
        return ResultUtils.success("ok");
    }

    /**
     * 删除接口信息
     * @param idRequest
     * @return
     */
    @ApiOperation("删除接口")
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> deleteInterfaceInfo(@Validated @RequestBody IdRequest idRequest){
        interfaceInfoService.deleteIntefaceInfo(idRequest);
        return ResultUtils.success("ok");
    }

    @ApiOperation("在线调用")
    @PostMapping("/testInvoke")
    public BaseResponse<String> onlineTest(@RequestBody TestInvokeRequest testInvokeRequest, HttpServletRequest request){

        return ResultUtils.success(interfaceInfoService.onlineTest(testInvokeRequest));
//        boolean b = innerUserInterfaceInvokeService.afterInvokeSuccess("1", "1", 2);
//        return ResultUtils.success(b);
    }

    @ApiOperation("获取接口详情")
    @GetMapping("/vo/{id}")
    public BaseResponse<InterfaceInfoVo> getVoById(@PathVariable String id) {
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVoById(id));
    }

    @ApiOperation("在线调用")
    @PostMapping("/test")
    public BaseResponse<String> test(){

        GeneralClientBuild clientBuild = new GeneralClientBuild("127.0.0.1:9009","5fa29e07dd817a839d9d3c78442391a1","56770f8cf6b9d00bd01a9a896938e232");
        BasicClient client = clientBuild.build();
        client.uri("/api/interface/basic/randomNum");

        client.addParam("min","1");
        client.addParam("max","10");

        String result = client.get();
        return ResultUtils.success(result);
//        boolean b = innerUserInterfaceInvokeService.afterInvokeSuccess("1", "1", 2);
//        return ResultUtils.success(b);
    }

}
