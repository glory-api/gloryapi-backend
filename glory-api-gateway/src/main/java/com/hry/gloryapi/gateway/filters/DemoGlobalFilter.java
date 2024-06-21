package com.hry.gloryapi.gateway.filters;

import com.hry.glory.common.enums.ErrorCode;
import com.hry.glory.common.exception.BusinessException;
import com.hry.gloryapi.common.exception.ApiBusinessException;
import com.hry.gloryapi.common.model.entity.InterfaceInfo;
import com.hry.gloryapi.common.model.entity.UserInterfaceInvokeEntity;
import com.hry.gloryapi.common.model.enums.InterfaceStatusEnum;
import com.hry.gloryapi.common.model.enums.UserInterfaceInvokeEnum;
import com.hry.gloryapi.common.model.enums.UserRoleEnum;
import com.hry.gloryapi.common.model.vo.UserVo;
import com.hry.gloryapi.common.service.InnerInterfaceInfoService;
import com.hry.gloryapi.common.service.InnerUserInterfaceInvokeService;
import com.hry.gloryapi.common.service.InnerUserService;
import com.hry.gloryapisdk.constant.HttpHeader;
import com.hry.gloryapisdk.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

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
    @DubboReference
    private InnerUserInterfaceInvokeService innerUserInterfaceInvokeService;
    @DubboReference
    private InnerUserService innerUserService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

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
        UserVo userVo = innerUserService.getOneByAccessKey(accessKey);
        if (userVo == null) {
            throw new ApiBusinessException(ErrorCode.FORBIDDEN_ERROR, "用户不存在");
        }
        if (userVo.getUserRole().equals(UserRoleEnum.BAN.getValue())) {
            throw new ApiBusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已禁用");
        }
        String secretKey = userVo.getSecretKey();//模拟查询获得secretKey

        //请求鉴权
        if (!SignUtils.checkSign(accessKey, secretKey, Long.parseLong(nonce), body, sign)) {
            //校验失败
            throw new ApiBusinessException(ErrorCode.NO_AUTH_ERROR, "鉴权失败，非法请求");
        }

        //查询接口是否存在
        String method = Objects.requireNonNull(request.getMethod()).toString();
        String uri = request.getPath().value();
        InterfaceInfo interfaceInfo = interfaceInfoService.getInterfaceInfoByUrlAndMethod(uri, method);
        if (interfaceInfo == null || interfaceInfo.getStatus().equals(InterfaceStatusEnum.OFF.getCode())) {
            throw new ApiBusinessException(ErrorCode.NOT_FOUND_ERROR, "请求接口不存在");
        }

        //用户是否有该接口调用权限
        UserInterfaceInvokeEntity userInterfaceInvoke = innerUserInterfaceInvokeService.getOneByUserIdAndInterfaceId(userVo.getId(), interfaceInfo.getId());
        if (userInterfaceInvoke != null && userInterfaceInvoke.getStatus().equals(UserInterfaceInvokeEnum.OFF.getCode())) {
            throw new ApiBusinessException(ErrorCode.FORBIDDEN_ERROR, "无接口调用权限");
        }

        RLock lock = redissonClient.getLock(userVo.getId());

        try {
            if(lock.tryLock()){
                UserVo userVo2 = innerUserService.getOneByAccessKey(accessKey);

                //用户积分是否充足
                if (userVo2.getIntegral().compareTo(0L) <= 0) {
                    throw new ApiBusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
                }

                boolean result = innerUserInterfaceInvokeService.afterInvokeSuccess(userVo.getId(), interfaceInfo.getId(), interfaceInfo.getIntegral());
                log.info("{}积分充足，调用次数增加，用户积分扣减", userVo2.getUserName());
            }else {
                throw new ApiBusinessException(ErrorCode.OPERATION_ERROR, "频繁调用");
            }
        } catch (Exception e) {
            if(!(e instanceof BusinessException)){
                log.error("调用失败",e);
                throw new ApiBusinessException(ErrorCode.OPERATION_ERROR,"调用失败");
            }
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                log.info("unLock:{}-{}",Thread.currentThread().getName(),Thread.currentThread().getId());
                lock.unlock();
            }
        }

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
        exchange.getAttributes().putAll(Map.of("userId",userVo.getId(),"interfaceId",interfaceInfo.getId(),"increaseScore",interfaceInfo.getIntegral().toString()));
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            //请求转发调用完成后
            if(!exchange.getResponse().getStatusCode().equals(HttpStatus.OK)){
                //调用失败 返还积分
                String msg = String.join(";", exchange.getAttribute("userId"), exchange.getAttribute("interfaceId"), exchange.getAttribute("increaseScore"));
                //如果exchange携带用户和接口信息，那么说明需要回补积分
                //使用MQ异步回补
                rocketMQTemplate.asyncSend("afterInvoke", msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("异步消息发送成功");
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("异步发送消息失败：原因{},消息内容{}",e.getMessage(),msg);
                    }
                });
            }
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }


}
