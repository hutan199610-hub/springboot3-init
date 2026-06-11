package com.example.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class DynamicConfig {

    @Value("${app.feature.new-dashboard:false}")
    private boolean newDashboardEnabled;

    @Value("${app.config.max-upload-size:10}")
    private int maxUploadSizeMB;

    public boolean isNewDashboardEnabled() {
        return newDashboardEnabled;
    }

    public int getMaxUploadSizeMB() {
        return maxUploadSizeMB;
    }
}
