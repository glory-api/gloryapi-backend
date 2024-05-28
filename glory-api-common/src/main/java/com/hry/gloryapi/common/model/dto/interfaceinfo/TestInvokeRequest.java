package com.hry.gloryapi.common.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: huangry
 * @create: 2024/5/16
 **/
@Data
public class TestInvokeRequest implements Serializable {
    private String interfaceId;
    private Map<String,Object> requestParams;

    @Data
    public static class Field {
        private String fieldName;
        private String value;
    }
}
