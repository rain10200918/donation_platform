package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 管理员审核记录类
 * 用于存储管理员对各种目标对象的审核信息
 */
public class AdminAudits {
    /** 审核记录唯一标识符 */
    private Long id;
    /** 审核类型，用于区分不同的审核业务场景 */
    private Integer auditType;
    /** 被审核目标对象的ID */
    private Long targetId;
    /** 执行审核的管理员ID */
    private Long adminId;
    /** 审核结果，记录审核通过或拒绝等结果状态 */
    private String auditResult;
    /** 审核原因，记录审核通过或拒绝的具体原因说明 */
    private String auditReason;
    /** 审核时间，记录审核操作执行的时间戳 */
    private LocalDateTime auditTime;
}

