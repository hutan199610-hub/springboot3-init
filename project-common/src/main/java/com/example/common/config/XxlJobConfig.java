package com.example.common.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "com.xxl.job.core.executor.impl.XxlJobSpringExecutor")
@ConditionalOnProperty(name = "xxljob.enabled", havingValue = "true")
public class XxlJobConfig {

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses("http://localhost:8080/xxl-job-admin");
        executor.setAppname("project-admin");
        executor.setPort(9999);
        executor.setLogRetentionDays(30);
        return executor;
    }
}
