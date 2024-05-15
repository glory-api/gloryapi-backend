package com.hry.gloryapi.gateway.filters;

import com.hry.gloryapi.gateway.filters.rewrite.ResponseRewriteFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author: huangry
 * @create: 2024/5/15
 **/
@Component
public class ResponseModifyFilter implements GlobalFilter, Ordered {
    @Resource
    private ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType contentType = request.getHeaders().getContentType();
//        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            //如果请求是application/json 格式
            return modifyResponseBodyGatewayFilterFactory
                .apply(
                    new ModifyResponseBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(byte[].class,byte[].class,new ResponseRewriteFunction()))
                .filter(exchange, chain);
//        }
//        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        //优先级需要高于 gateway过滤器链中对自带的对响应体处理的过滤器
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER-1;
    }
}
