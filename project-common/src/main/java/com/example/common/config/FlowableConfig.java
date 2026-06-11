package com.example.common.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.flowable.spring.SpringProcessEngineConfiguration")
@ConditionalOnProperty(name = "flowable.enabled", havingValue = "true")
public class FlowableConfig {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> flowableConfigurer() {
        return configuration -> {
            configuration.setDatabaseSchemaUpdate("true");
            configuration.setAsyncExecutorActivate(false);
        };
    }
}
