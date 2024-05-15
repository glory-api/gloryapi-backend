package com.hry.gloryapi.gateway.filters.rewrite;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.gloryapi.common.exception.ApiBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 响应重写功能，使用该类，打印响应日志
 * @author: huangry
 * @create: 2024/5/15
 **/
@Slf4j
public class ResponseRewriteFunction implements RewriteFunction<byte[],byte[]> {
    @Override
    public Publisher<byte[]> apply(ServerWebExchange serverWebExchange, byte[] bytes) {

        if(!serverWebExchange.getResponse().getStatusCode().equals(HttpStatus.OK)){
            log.error("接口响应内容----->{}",new String(bytes, StandardCharsets.UTF_8));
            throw new ApiBusinessException(ErrorCode.OPERATION_ERROR,"接口调用失败");
        }
        return Mono.just(bytes);
    }
}
