package com.hry.gloryapi.backend.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hry.gloryapi.backend.common.IdRequest;
import com.hry.gloryapi.backend.common.PageResponse;
import com.hry.gloryapi.backend.constant.CommonConstant;
import com.hry.gloryapi.backend.model.enums.InterfaceStatusEnum;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.utils.ThrowUtils;
import com.hry.gloryapi.backend.mapper.InterfaceInfoMapper;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.hry.gloryapi.backend.model.entity.InterfaceInfo;
import com.hry.gloryapi.backend.model.vo.InterfaceInfoVo;
import com.hry.gloryapi.backend.model.vo.InterfaceRequestParam;
import com.hry.gloryapi.backend.model.vo.InterfaceResponseParam;
import com.hry.gloryapi.backend.service.InterfaceInfoService;
import com.hry.gloryapi.backend.utils.SqlUtils;
import com.hry.gloryapi.backend.utils.UserContext;
import com.hry.glory.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
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

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public PageResponse<InterfaceInfoVo> listInterfaceInfoVoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        Page<InterfaceInfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(), interfaceInfoQueryRequest.getPageSize());
        QueryWrapper<InterfaceInfo> queryWrapper = getListQueryWrapper(interfaceInfoQueryRequest);
        Page<InterfaceInfo> resultPage = interfaceInfoMapper.selectPage(page, queryWrapper);
        return toPageVo(resultPage);
    }

    @Override
    public Long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceInfoAddRequest, interfaceInfo, "requestParams", "responseParams");
        interfaceInfo.setRequestParams(GSON.toJson(interfaceInfoAddRequest.getRequestParams(),new TypeToken<List<InterfaceRequestParam>>() {}.getType()));
        interfaceInfo.setResponseParams(GSON.toJson(interfaceInfoAddRequest.getResponseParams(),new TypeToken<List<InterfaceRequestParam>>() {}.getType()));
        interfaceInfo.setUserid(UserContext.getLoginUser().getId());
        interfaceInfoMapper.insert(interfaceInfo);
        return interfaceInfo.getId();
    }

    @Override
    public int updateStatus(IdRequest idRequest) {
        //检验接口是否存在
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(idRequest.getId());
        if(Objects.isNull(interfaceInfo)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //发布接口 要测试接口
        if(interfaceInfo.getStatus().equals(InterfaceStatusEnum.OFF.getCode())){
            // TODO: 2023/10/24 调用接口
            interfaceInfo.setStatus("1");
            log.info("{}接口发布",interfaceInfo.getId());
        }else {
            interfaceInfo.setStatus("0");
            log.info("{}接口下线",interfaceInfo.getId());
        }

        int result = interfaceInfoMapper.updateById(interfaceInfo);
        ThrowUtils.throwIf(result<=0,ErrorCode.SYSTEM_ERROR,"更新状态失败，数据库错误");
        return result;
    }

    @Override
    public int updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        InterfaceInfo oldInterfaceInfo = interfaceInfoMapper.selectById(interfaceInfoUpdateRequest.getId());
        ThrowUtils.throwIf(Objects.isNull(oldInterfaceInfo),ErrorCode.NOT_FOUND_ERROR);
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceInfoUpdateRequest, newInterfaceInfo, "requestParams", "responseParams");
        newInterfaceInfo.setRequestParams(GSON.toJson(interfaceInfoUpdateRequest.getRequestParams(),new TypeToken<List<InterfaceRequestParam>>() {}.getType()));
        newInterfaceInfo.setResponseParams(GSON.toJson(interfaceInfoUpdateRequest.getResponseParams(),new TypeToken<List<InterfaceRequestParam>>() {}.getType()));
        int result = interfaceInfoMapper.updateById(newInterfaceInfo);
        ThrowUtils.throwIf(result<=0,ErrorCode.SYSTEM_ERROR,"更新接口信息失败，数据库错误");
        return result;
    }

    @Override
    public int deleteIntefaceInfo(IdRequest idRequest) {
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(idRequest.getId());
        ThrowUtils.throwIf(Objects.isNull(interfaceInfo),ErrorCode.NOT_FOUND_ERROR);
        if(interfaceInfo.getIsDelete() == 0){
            int result = interfaceInfoMapper.deleteById(idRequest.getId());
            ThrowUtils.throwIf(result<=0,ErrorCode.SYSTEM_ERROR,"删除接口信息失败，数据库错误");
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
        if(!SqlUtils.validSortField(sortField,InterfaceInfo.class)){
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
        wrapper.orderBy(SqlUtils.validSortField(sortField,InterfaceInfo.class), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return wrapper;
    }

    @Override
    public PageResponse<InterfaceInfoVo> toPageVo(Page<InterfaceInfo> page) {
        PageResponse<InterfaceInfoVo> pageVo= new PageResponse<>();
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
                new TypeToken<List<InterfaceRequestParam>>() {}.getType()));
        interfaceInfoVo.setResponseParams(GSON.fromJson(interfaceInfo.getResponseParams(),
                new TypeToken<List<InterfaceResponseParam>>() {}.getType()));
        return interfaceInfoVo;
    }
}
