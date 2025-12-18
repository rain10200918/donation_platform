package com.example.donationplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.donationplatform.mapper")
public class DonationPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(DonationPlatformApplication.class, args);
    }

}
