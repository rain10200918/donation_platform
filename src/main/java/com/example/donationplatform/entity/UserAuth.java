package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户认证信息实体类
 * 用于存储和管理用户的实名认证相关信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuth {
    /**
     * 认证记录唯一标识ID
     */
    private Long id;

    /**
     * 关联的用户ID
     */
    private Long userId;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 用户身份证号码
     */
    private String idNumber;

    /**
     * 认证状态：0-待审核，1-认证通过，2-认证拒绝
     */
    private Integer authStatus;

    /**
     * 认证拒绝原因，当authStatus为2时填写
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

