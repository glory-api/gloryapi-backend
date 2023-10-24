package com.hry.gloryapi.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hry.gloryapi.backend.common.IdRequest;
import com.hry.gloryapi.backend.common.PageResponse;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.hry.gloryapi.backend.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.hry.gloryapi.backend.model.entity.InterfaceInfo;
import com.hry.gloryapi.backend.model.vo.InterfaceInfoVo;

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
    public PageResponse<InterfaceInfoVo> listInterfaceInfoVoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 添加分页信息
     * @param interfaceInfoAddRequest
     * @return
     */
    Long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest);

    /**
     * 修改接口状态
     * @param idRequest
     * @return
     */
    int updateStatus(IdRequest idRequest);

    /**
     * 修改接口信息
     * @param interfaceInfoUpdateRequest
     * @return
     */
    int updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest);

    /**
     * 删除接口信息
     * @param idRequest
     * @return
     */
    int deleteIntefaceInfo(IdRequest idRequest);

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
    PageResponse<InterfaceInfoVo> toPageVo(Page<InterfaceInfo> page);

    /**
     * 实体类转VO
     * @param interfaceInfo
     * @return
     */
    InterfaceInfoVo toInterfaceInfoVo(InterfaceInfo interfaceInfo);

}
