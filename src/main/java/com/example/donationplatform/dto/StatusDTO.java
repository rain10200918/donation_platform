package com.example.donationplatform.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
@Data
public class StatusDTO {
    private Long userId;
    private String status;
}
