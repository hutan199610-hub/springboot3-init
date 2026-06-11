package com.example.common.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.apache.rocketmq.client.producer.DefaultMQProducer")
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true")
public class RocketMQConfig {

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer("project-producer-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.setSendMsgTimeout(10000);
        producer.setRetryTimesWhenSendFailed(3);
        return producer;
    }
}
