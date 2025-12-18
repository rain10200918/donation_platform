package com.example.donationplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        // 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();

        // 关键修正：使用allowedOriginPatterns替代allowedOrigins
        // 当allowCredentials为true时，不能使用addAllowedOrigin("*")
        config.addAllowedOriginPattern("http://localhost:*");

        // 允许携带凭证信息（如Cookie）
        config.setAllowCredentials(true);

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有HTTP方法
        config.addAllowedMethod("*");

        // 设置预检请求的缓存时间（1小时）
        config.setMaxAge(3600L);  // 补充之前缺失的配置


        // 创建基于URL的CORS配置源
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();

        // 注册全局CORS配置，应用于所有路径
        configSource.registerCorsConfiguration("/**", config);

        // 返回配置好的CORS过滤器
        return new CorsFilter(configSource);
    }
}
