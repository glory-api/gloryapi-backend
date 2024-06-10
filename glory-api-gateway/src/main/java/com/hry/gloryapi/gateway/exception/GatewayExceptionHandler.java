package com.hry.gloryapi.gateway.exception;

import com.alibaba.nacos.shaded.com.google.common.base.Throwables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.glory.common.model.dto.BaseResponse;
import com.hry.glory.common.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: QiMu
 * @Date: 2023/09/10 09:35:08
 * @Version: 1.0
 * @Description: 错误web异常处理程序
 */
@Configuration
@Slf4j
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //如果响应已完成
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        DataBufferFactory bufferFactory = response.bufferFactory();
        int bodyCode;
        String errorMsg = "";
        //如果是业务异常，修改响应状态码为无权限，返回业务异常异常信息
        if(ex instanceof BusinessException){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            bodyCode = ((BusinessException) ex).getCode();
            errorMsg = ex.getMessage();
        }else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            bodyCode = ErrorCode.SYSTEM_ERROR.getCode();
            errorMsg = "接口服务异常";
        }

//        log.error("[网关异常处理]");
        log.error("[网关异常处理]-->{}", Throwables.getStackTraceAsString(ex));

        BaseResponse<Boolean> error = ResultUtils.error(bodyCode, errorMsg);

        try {
            byte[] errorBytes = objectMapper.writeValueAsBytes(error);
            DataBuffer dataBuffer = bufferFactory.wrap(errorBytes);
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化异常：{}", e.getMessage());
            return Mono.error(e);
        }
    }
}