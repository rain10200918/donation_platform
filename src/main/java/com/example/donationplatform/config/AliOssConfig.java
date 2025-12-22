package com.example.donationplatform.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@Slf4j
public class AliOssConfig {
    @Autowired
    private AliOssProperties aliOssProperties;
    @Bean
    public OSS ossClient(){
        OSS client = new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret()
        );
        log.info("阿里云 OSS 客户端初始化成功");
        return client;
    }
}
