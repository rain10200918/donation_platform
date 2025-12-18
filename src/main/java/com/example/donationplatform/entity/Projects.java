package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目信息实体类
 *
 * 该类用于表示众筹平台中的项目信息，包含了项目的基本属性、状态信息和时间信息。
 * 使用Lombok注解简化了构造函数和getter/setter方法的生成。
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Projects {
    /** 项目唯一标识ID */
    private Long id;

    /** 项目类型 */
    private Integer projectType;

    /** 项目标题 */
    private String title;

    /** 项目摘要描述 */
    private String summary;

    /** 项目发起人ID */
    private Long initiatorId;

    /** 项目目标金额 */
    private BigDecimal targetAmount;

    /** 项目已筹金额 */
    private BigDecimal raisedAmount;

    /** 项目状态 */
    private Integer status;

    /** 项目审核状态 */
    private Integer auditStatus;

    /** 项目开始时间 */
    private LocalDateTime startTime;

    /** 项目结束时间 */
    private LocalDateTime endTime;

    /** 项目拒绝原因 */
    private String rejectReason;

    /** 项目创建时间 */
    private LocalDateTime createTime;

    /** 项目更新时间 */
    private LocalDateTime updateTime;
}

