package com.hry.gloryapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hry.gloryapi.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.model.entity.InterfaceInfo;
import com.hry.gloryapi.model.vo.InterfaceInfoVo;

import java.util.List;

/**
* @author lenovo
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-10-13 14:52:34
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 分页获取接口信息
     * @param interfaceInfoQueryRequest
     * @return
     */
    public Page<InterfaceInfoVo> listInterfaceInfoVoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 获取查询包装类
     * @param interfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 分页查询结果转换为Vo类的分页查询结果
     * @param page
     * @return
     */
    Page<InterfaceInfoVo> pagetoPageVo(Page<InterfaceInfo> page);
}
