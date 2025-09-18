package com.fit2cloud.itsm.service.impl.consume;

import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhaoqian
 * @title: RabbitApplicationRunner
 * @projectName fit2cloud2.0-itsm-integration
 * @description: Rabbit消息队列启动类
 * @date 2022/7/510:51 上午
 */
@Component
public class RabbitApplicationRunner implements ApplicationRunner {

    private final RabbitListenerEndpointRegistry registry;

    @Value("${rabbit.openTimingConsume:''}")
    private String openTimingConsume;

    @Autowired
    public RabbitApplicationRunner(RabbitListenerEndpointRegistry registry) {
        this.registry =  registry;
    }

    /**
     * 不启用定时消费时才会执行，默认实时消费
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (StringUtils.isBlank(openTimingConsume) || StringUtils.equals(openTimingConsume, "false")) {
            LogUtil.info("当前不启用定时消费，默认实时消费...");
            String[] listeners = new String[] { "rabbit_listener" };
            for (String listenerId: listeners) {
                MessageListenerContainer container = registry.getListenerContainer(listenerId);
                if (container == null) {
                    LogUtil.info("listener:" + listenerId + " is null.");
                } else {
                    if (container.isRunning()) {
                        LogUtil.info("listener:" + listenerId + " is running.");
                    } else {
                        container.start();
                        LogUtil.info("listener:" + listenerId + " started.");
                    }
                }
            }
        }
    }

    /**
     * 定时启动消费队列
     * 默认每天凌晨一点钟启动消费，只有在启用定时消费时才会执行，不启用则默认实时消费
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void openRabbiitMqJob() {
        if (StringUtils.isNotBlank(openTimingConsume) || StringUtils.equals(openTimingConsume, "true")) {
            LogUtil.info("当前启用定时消费，定时启动消费队列...");
            String[] listeners = new String[] { "rabbit_listener" };
            for (String listenerId : listeners) {
                MessageListenerContainer container = registry.getListenerContainer(listenerId);
                if (container == null) {
                    LogUtil.info("listener:" + listenerId + " is null.");
                } else {
                    if (container.isRunning()) {
                        LogUtil.info("listener:" + listenerId + " is running.");
                    } else {
                        container.start();
                        LogUtil.info("listener:" + listenerId + " started.");
                    }
                }
            }
        }
    }

    /**
     * 定时关闭消费队列
     * 默认每天凌晨五点钟关闭消费，只有在启用定时消费时才会执行，不启用则默认实时消费
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void closeRabbiitMqJob() {
        if (StringUtils.isNotBlank(openTimingConsume) || StringUtils.equals(openTimingConsume, "true")) {
            LogUtil.info("当前启用定时消费，定时关闭消费队列...");
            String[] listeners = new String[] { "rabbit_listener" };
            for (String listenerId : listeners) {
                MessageListenerContainer container = registry.getListenerContainer(listenerId);
                if (container == null) {
                    LogUtil.info("listener:" + listenerId + " is null.");
                } else {
                    if (container.isRunning()) {
                        container.stop();
                        LogUtil.info("listener:" + listenerId + " stoped.");
                    }
                }
            }
        }
    }

}
