package com.bjpowernode.web.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties( prefix = "txcloud")
public class CloudConfig {
    private String secretId;
    private String secretKey;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "CloudConfig{" +
                "secretId='" + secretId + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}
