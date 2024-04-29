package com.hry.gloryapi.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求路径-----{}", request.getPath());
        log.info("请求路径-----{}", request.getURI().getRawPath());

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

        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            //请求转发调用完成后

            log.info("请求结束 {}",exchange.getResponse().getStatusCode().value());
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
