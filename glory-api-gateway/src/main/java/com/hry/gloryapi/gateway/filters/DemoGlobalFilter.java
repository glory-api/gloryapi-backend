package com.hry.gloryapi.gateway.filters;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.gloryapi.common.service.InnerInterfaceInfoService;
import com.hry.gloryapisdk.constant.HttpHeader;
import com.hry.gloryapisdk.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author: huangry
 * @create: 2024/4/28
 **/
@Component
@Slf4j
public class DemoGlobalFilter implements GlobalFilter, Ordered {
//    @Resource
//    private ApplicationEventPublisher publisher;

//    @Resource
//    private InMemoryRouteDefinitionRepository routeDefinitionLocator;
    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        ServerHttpRequest request = exchange.getRequest();
        //记录请求日志
        log.info("请求标识----->{}; " +
            "请求路径----->{}; " +
            "请求方法----->{}; " +
            "请求参数----->{}; ", request.getId(), request.getPath().value(), request.getMethodValue(), request.getQueryParams());

        //鉴权、校验ak sk 等参数
        //获取请求头中的信息
        String accessKey = request.getHeaders().getFirst(HttpHeader.AK);
        String nonce = request.getHeaders().getFirst(HttpHeader.NONCE);
        String sign = request.getHeaders().getFirst(HttpHeader.SIGN);
        String body = request.getHeaders().getFirst(HttpHeader.BODY);

        //根据AK获取用户信息
        String secretKey = "11";//模拟查询获得secretKey

        //查看用户是否有接口的调用次数或权限。
        interfaceInfoService.getInterfaceInfoByUrlAndMethod("1","1");

        if (!SignUtils.checkSign(accessKey, secretKey, Long.parseLong(nonce), body, sign)) {
            //校验失败
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非法请求");
        }

        //查询接口是否存在


        //修改 当前匹配到的route信息
//        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR,route);
//        RouteDefinition routeDefinition = new RouteDefinition();
//        routeDefinition.setId(route.getId());
//        routeDefinition.setUri(URI.create("http://127.0.0.1:9003"));
//        routeDefinition.setOrder(route.getOrder());
//        routeDefinition.setMetadata(route.getMetadata());
//
//        Route route1 = Route.async(routeDefinition).asyncPredicate(route.getPredicate()).replaceFilters(route.getFilters()).build();
//        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR,route1);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            //请求转发调用完成后
            //调用成功，接口调用次数+1
            log.info("请求结束 {}", exchange.getResponse().getStatusCode().value());
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
