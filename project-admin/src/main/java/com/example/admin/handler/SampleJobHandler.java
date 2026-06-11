package com.example.admin.handler;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleJobHandler {

    private static final Logger log = LoggerFactory.getLogger(SampleJobHandler.class);

    @XxlJob("sampleJobHandler")
    public void execute() {
        log.info("XXL-JOB 示例任务开始执行");
        log.info("XXL-JOB 示例任务执行完成");
    }
}
