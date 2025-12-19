package com.example.donationplatform.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserVO {
    private Long id;
    private String nickName;
    private String avatar;
    private Integer status; // 0-禁用，1-正常

    // --- 逻辑增强字段 ---
    private Integer isDonor; // 需要查询 donation 表，如果有记录则返回 1
    private BigDecimal totalDonated; // 聚合查询该用户累计捐赠的金额总和
    private String lastLoginTime; // 记录用户最后一次交互的时间
}
