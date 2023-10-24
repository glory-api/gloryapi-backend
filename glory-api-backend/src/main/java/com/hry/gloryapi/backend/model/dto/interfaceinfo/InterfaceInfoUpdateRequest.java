package com.hry.gloryapi.backend.model.dto.interfaceinfo;



import com.hry.gloryapi.backend.model.vo.InterfaceRequestParam;
import com.hry.gloryapi.backend.model.vo.InterfaceResponseParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 接口信息修改请求对象
 * @author: huangry
 * @create: 2023/10/13
 **/
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口id
     */
    @ApiModelProperty(value = "接口ID", required = true)
    @NotNull(message = "接口id不能为空")
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称",required = true)
    @NotBlank(message = "接口名称不能为空")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 接口地址
     */
    @ApiModelProperty(value = "接口地址",required = true)
    @NotBlank(message = "接口地址不能为空")
    private String url;

    /**
     * 请求参数
     */
    @ApiModelProperty("请求参数")
    private List<InterfaceRequestParam> requestParams;

    /**
     * 请求头
     */
    @ApiModelProperty("请求头")
    private String requestHeader;

    /**
     * 响应参数
     */
    @ApiModelProperty(value = "响应参数",required = true)
    @Valid
    @NotEmpty(message = "响应参数不能为空")
    private List<InterfaceResponseParam> responseParams;

    /**
     * 响应头
     */
    @ApiModelProperty("响应头")
    private String responseHeader;

    /**
     * 请求类型
     */
    @ApiModelProperty(value = "请求类型",required = true)
    @NotBlank(message = "请求类型不能为空")
    private String method;

}
