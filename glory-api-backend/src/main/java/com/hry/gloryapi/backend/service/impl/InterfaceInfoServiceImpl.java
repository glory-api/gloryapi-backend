package com.hry.gloryapi.backend.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.utils.ThrowUtils;
import com.hry.gloryapi.backend.constant.CommonConstant;
import com.hry.gloryapi.backend.mapper.InterfaceInfoMapper;
import com.hry.gloryapi.backend.service.InterfaceInfoService;
import com.hry.gloryapi.backend.utils.SqlUtils;
import com.hry.gloryapi.backend.utils.UserContext;
import com.hry.gloryapi.common.common.IdRequest;
import com.hry.gloryapi.common.common.PageResponse;
import com.hry.gloryapi.common.model.dto.interfaceinfo.*;
import com.hry.gloryapi.common.model.entity.InterfaceInfo;
import com.hry.gloryapi.common.model.entity.User;
import com.hry.gloryapi.common.model.enums.InterfaceStatusEnum;
import com.hry.gloryapi.common.model.vo.InterfaceInfoVo;
import com.hry.gloryapisdk.client.BasicClient;
import com.hry.gloryapisdk.client.build.GeneralClientBuild;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huangry
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-10-13 14:52:34
 */
@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {
    private static final Gson GSON = new Gson();

    @Cacheable(value = "interfacePage", key = "#interfaceInfoQueryRequest.hashCode()")
    @Override
    public PageResponse<InterfaceInfoVo> listInterfaceInfoVoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        Page<InterfaceInfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(), interfaceInfoQueryRequest.getPageSize());
        QueryWrapper<InterfaceInfo> queryWrapper = getListQueryWrapper(interfaceInfoQueryRequest);
        Page<InterfaceInfo> resultPage = baseMapper.selectPage(page, queryWrapper);

        return toPageVo(resultPage);
    }

    @Override
    public String addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceInfoAddRequest, interfaceInfo, "requestParams", "responseParams");
        converParams(interfaceInfo, interfaceInfoAddRequest.getRequestParams(), interfaceInfoAddRequest.getResponseParams());
        interfaceInfo.setUserid(UserContext.getLoginUser().getId());
        baseMapper.insert(interfaceInfo);
        return interfaceInfo.getId();
    }

    @Override
    public int updateStatus(IdRequest idRequest) {
        //检验接口是否存在
        InterfaceInfo interfaceInfo = baseMapper.selectById(idRequest.getId());
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //发布接口 要测试接口
        if (interfaceInfo.getStatus().equals(InterfaceStatusEnum.OFF.getCode())) {
            // TODO: 2023/10/24 调用接口
            interfaceInfo.setStatus("1");
            log.info("{}接口发布", interfaceInfo.getId());
        } else {
            interfaceInfo.setStatus("0");
            log.info("{}接口下线", interfaceInfo.getId());
        }

        int result = baseMapper.updateById(interfaceInfo);
        ThrowUtils.throwIf(result <= 0, ErrorCode.SYSTEM_ERROR, "更新状态失败，数据库错误");
        return result;
    }

    @Override
    public int updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        InterfaceInfo oldInterfaceInfo = baseMapper.selectById(interfaceInfoUpdateRequest.getId());
        ThrowUtils.throwIf(Objects.isNull(oldInterfaceInfo), ErrorCode.NOT_FOUND_ERROR);
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceInfoUpdateRequest, newInterfaceInfo, "requestParams", "responseParams");
        converParams(newInterfaceInfo, interfaceInfoUpdateRequest.getRequestParams(), interfaceInfoUpdateRequest.getResponseParams());
        int result = baseMapper.updateById(newInterfaceInfo);
        ThrowUtils.throwIf(result <= 0, ErrorCode.SYSTEM_ERROR, "更新接口信息失败，数据库错误");
        return result;
    }

    private void converParams(InterfaceInfo source, List<InterfaceRequestParam> requestList, List<InterfaceResponseParam> responseParamList) {
        if (!CollectionUtils.isEmpty(requestList)) {
            source.setRequestParams(GSON.toJson(requestList, new TypeToken<List<InterfaceRequestParam>>() {
            }.getType()));
        }
        if (!CollectionUtils.isEmpty(requestList)) {
            source.setResponseParams(GSON.toJson(responseParamList, new TypeToken<List<InterfaceResponseParam>>() {
            }.getType()));
        }
    }

    @Override
    public int deleteIntefaceInfo(IdRequest idRequest) {
        InterfaceInfo interfaceInfo = baseMapper.selectById(idRequest.getId());
        ThrowUtils.throwIf(Objects.isNull(interfaceInfo), ErrorCode.NOT_FOUND_ERROR);
        if (interfaceInfo.getIsDelete() == 0) {
            int result = baseMapper.deleteById(idRequest.getId());
            ThrowUtils.throwIf(result <= 0, ErrorCode.SYSTEM_ERROR, "删除接口信息失败，数据库错误");
            return result;
        }
        return 0;
    }


    private QueryWrapper<InterfaceInfo> getListQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        //获取查询参数
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        String status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        //排序默认 创建时间 降序
        if (!SqlUtils.validSortField(sortField, InterfaceInfo.class)) {
            sortField = "createTime";
            sortOrder = CommonConstant.SORT_ORDER_DESC;
        }
        //封装查询包装类
        QueryWrapper<InterfaceInfo> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);
        wrapper.like(StringUtils.isNotBlank(description), "description", description);
        wrapper.like(StringUtils.isNotBlank(url), "url", url);
        wrapper.eq(StringUtils.isNotBlank(status), "status", status);
        wrapper.eq(StringUtils.isNotBlank(method), "method", method);
        wrapper.orderBy(SqlUtils.validSortField(sortField, InterfaceInfo.class), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return wrapper;
    }

    @Override
    public PageResponse<InterfaceInfoVo> toPageVo(Page<InterfaceInfo> page) {
        PageResponse<InterfaceInfoVo> pageVo = new PageResponse<>();
        //查询结果为空
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return pageVo;
        }
        BeanUtil.copyProperties(page, pageVo, "records");
        //封装查询结果中每一个VO
        List<InterfaceInfoVo> collect = page.getRecords().stream().map(o -> toInterfaceInfoVo(o)).collect(Collectors.toList());
        BeanUtil.copyProperties(page, pageVo, "records");
        return pageVo.setRecords(collect);
    }

    @Override
    public InterfaceInfoVo toInterfaceInfoVo(InterfaceInfo interfaceInfo) {
        InterfaceInfoVo interfaceInfoVo = new InterfaceInfoVo();
        BeanUtil.copyProperties(interfaceInfo, interfaceInfoVo, "requestParams", "responseParams");
        //json串转集合对象
        interfaceInfoVo.setRequestParams(GSON.fromJson(interfaceInfo.getRequestParams(),
            new TypeToken<List<InterfaceRequestParam>>() {
            }.getType()));
        interfaceInfoVo.setResponseParams(GSON.fromJson(interfaceInfo.getResponseParams(),
            new TypeToken<List<InterfaceResponseParam>>() {
            }.getType()));
        return interfaceInfoVo;
    }

    @Override
    public String onlineTest(TestInvokeRequest testInvokeRequest) {
        //校验参数
        if (ObjectUtils.anyNull(testInvokeRequest, testInvokeRequest.getInterfaceId()) || StringUtils.isBlank(testInvokeRequest.getInterfaceId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //校验接口是否存在
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceInfo::getId, testInvokeRequest.getInterfaceId());
        InterfaceInfo interfaceInfo = this.getOne(wrapper);
        if (interfaceInfo == null || InterfaceStatusEnum.isOff(interfaceInfo.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }

        /*
        防止恶意刷在线调用接口，导致积分超扣的问题，加锁
         */

        //校验用户积分是否充足
        User loginUser = UserContext.getLoginUser();
        if (StringUtils.isAnyBlank(loginUser.getAccessKey(), loginUser.getSecretKey())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请先获取开发者凭证");
        }
        if (loginUser.getIntegral() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
        }


        //使用SDK
        Map<String, Object> fileds = testInvokeRequest.getRequestParams();
        GeneralClientBuild clientBuild = new GeneralClientBuild("127.0.0.1:9009",loginUser.getAccessKey(),loginUser.getSecretKey());
        BasicClient client = clientBuild.build();
        client.uri(interfaceInfo.getUrl());
        if(!CollectionUtils.isEmpty(fileds)){
            client.addParams(fileds);
        }

        String result = interfaceInfo.getMethod().equals("GET") ? client.get() : client.post();

        return result;
    }

    @Override
    public InterfaceInfoVo getInterfaceInfoVoById(String id) {
        InterfaceInfo interfaceInfo = baseMapper.selectById(id);
        ThrowUtils.throwIf(Objects.isNull(interfaceInfo), ErrorCode.NOT_FOUND_ERROR);
        return toInterfaceInfoVo(interfaceInfo);
    }
}
