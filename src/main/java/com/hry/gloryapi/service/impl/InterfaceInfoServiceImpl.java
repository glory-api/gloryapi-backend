package com.hry.gloryapi.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hry.gloryapi.constant.CommonConstant;
import com.hry.gloryapi.mapper.InterfaceInfoMapper;
import com.hry.gloryapi.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.model.entity.InterfaceInfo;
import com.hry.gloryapi.model.vo.InterfaceInfoVo;

import com.hry.gloryapi.service.InterfaceInfoService;
import com.hry.gloryapi.utils.SqlUtils;
import com.hry.gloryapi.utils.UserContext;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author huangry
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-10-13 14:52:34
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {


    @Override
    public Page<InterfaceInfoVo> listInterfaceInfoVoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        Page<InterfaceInfo> page = new Page<>(interfaceInfoQueryRequest.getCurrent(),interfaceInfoQueryRequest.getPageSize());
        if(!StringUtils.equals(UserContext.getLoginUser().getUserRole(),"admin")){
            //不是管理员，看到的是启用的接口
            interfaceInfoQueryRequest.setStatus(2);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = getQueryWrapper(interfaceInfoQueryRequest);
        Page<InterfaceInfo> resultPage = page(page, queryWrapper);
        return pagetoPageVo(resultPage);
    }

    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        Integer status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();

        QueryWrapper<InterfaceInfo> wrapper = new QueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        wrapper.like(StringUtils.isNotBlank(description),"description",description);
        wrapper.like(StringUtils.isNotBlank(url),"url",url);
        wrapper.eq(!Objects.isNull(status),"status",status);
        wrapper.eq(StringUtils.isNotBlank(method),"method",method);
        wrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return null;
    }

    @Override
    public Page<InterfaceInfoVo> pagetoPageVo(Page<InterfaceInfo> page) {
        Page<InterfaceInfoVo> pageVo = new Page<>();
        if(CollectionUtils.isEmpty(page.getRecords())){
            BeanUtil.copyProperties(page,pageVo,"records");
            return pageVo.setRecords(new ArrayList<>());
        }
        List<InterfaceInfoVo> collect = page.getRecords().stream().map(o -> {
            InterfaceInfoVo interfaceInfoVo = new InterfaceInfoVo();
            BeanUtil.copyProperties(o, interfaceInfoVo);
            return interfaceInfoVo;
        }).collect(Collectors.toList());
        BeanUtil.copyProperties(page,pageVo,"records");
        return pageVo.setRecords(collect);

    }
}
