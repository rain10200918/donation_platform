package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * Donations类用于表示捐赠信息
 * 该类包含了捐赠记录的所有基本信息，包括捐赠者、项目、金额等信息
 */
public class Donations {
    /** 捐赠记录的唯一标识ID */
    private Long id;

    /** 捐赠用户的ID */
    private Long userId;

    /** 捐赠项目的ID */
    private Long projectId;

    /** 捐赠金额 */
    private BigDecimal amount;

    /** 是否匿名捐赠标识，0-非匿名，1-匿名 */
    private Integer isAnonymous;

    /** 捐赠状态，用于标识捐赠的处理状态 */
    private Integer donationStatus;

    /** 捐赠时间 */
    private LocalDateTime donationTime;
}

