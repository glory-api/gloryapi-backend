package com.hry.gloryapi.backend.service.impl;

import com.hry.gloryapi.backend.service.impl.inner.InnerUserInterfaceInvokeServiceImpl;
import com.hry.gloryapi.common.service.InnerUserInterfaceInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: huangry
 * @create: 2024/6/21
 **/
@Slf4j
@Component
public class InterfaceInfoMqConsumerService {
    @Resource
    private InnerUserInterfaceInvokeService innerUserInterfaceInvokeService;

    @Service
    @RocketMQMessageListener(topic = "afterInvoke", consumerGroup = "InterfaceInvoke")
    public class UserInterfaceInvoke implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            log.info("消费者获取信息成功：{}", message);
            String[] split = message.split(";");
            innerUserInterfaceInvokeService.afterInvokeFailed(split[0],split[1],Long.valueOf(split[2]));
        }
    }
}
