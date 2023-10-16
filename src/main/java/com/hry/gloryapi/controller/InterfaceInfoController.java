package com.hry.gloryapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.hry.gloryapi.annotation.AuthCheck;
import com.hry.gloryapi.common.BaseResponse;
import com.hry.gloryapi.common.DeleteRequest;
import com.hry.gloryapi.common.ErrorCode;
import com.hry.gloryapi.common.ResultUtils;
import com.hry.gloryapi.constant.UserConstant;
import com.hry.gloryapi.exception.BusinessException;
import com.hry.gloryapi.exception.ThrowUtils;
import com.hry.gloryapi.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.model.dto.post.PostAddRequest;
import com.hry.gloryapi.model.dto.post.PostEditRequest;
import com.hry.gloryapi.model.dto.post.PostQueryRequest;
import com.hry.gloryapi.model.dto.post.PostUpdateRequest;
import com.hry.gloryapi.model.entity.InterfaceInfo;
import com.hry.gloryapi.model.entity.Post;
import com.hry.gloryapi.model.entity.User;
import com.hry.gloryapi.model.vo.InterfaceInfoVo;
import com.hry.gloryapi.model.vo.PostVO;
import com.hry.gloryapi.service.InterfaceInfoService;
import com.hry.gloryapi.service.PostService;
import com.hry.gloryapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

//    @PostMapping("/add")
//    public BaseResponse<Long> addInterfaceInfo(@RequestBody ){
//
//    }

    /**
     * 分页获取接口信息
     * @param interfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/listVoByPage")
    public BaseResponse<Page<InterfaceInfoVo>> listInterfaceInfoVoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest){
        if(interfaceInfoQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.listInterfaceInfoVoByPage(interfaceInfoQueryRequest));
    }






}
