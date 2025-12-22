package com.example.donationplatform.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletLog {
    private Long id;
    private Long userId;
    private Integer type; // 1充值 2捐款扣款 3退款增加
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String remark;
    private LocalDateTime createTime;
}
