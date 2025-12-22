package com.example.donationplatform.dto;

import lombok.Data;

@Data
public class AuditDTO {
    private Long auditStatus;
    private Long id;
    private String rejectReason;
}
