package com.example.donationplatform.util;

import com.aliyun.oss.OSS;
import com.example.donationplatform.config.AliOssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliOssUtil {
    private final OSS ossClient;
    private final AliOssProperties aliOssProperties;

    /**
     * 文件上传
     *
     * @param bytes      文件字节
     * @param objectName 存储在 OSS 的路径，例如 avatar/123.jpg
     * @return 文件访问 URL
     */
    public String upload(byte[] bytes, String objectName) {

        try {
            ossClient.putObject(aliOssProperties.getBucketName(), objectName, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("OSS 上传失败：" + e.getMessage());
        }

        // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
        String url = "https://" + aliOssProperties.getBucketName() + "." +
                aliOssProperties.getEndpoint() + "/" + objectName;
        log.info("文件上传到: {}", url);
        return url;
    }
}
