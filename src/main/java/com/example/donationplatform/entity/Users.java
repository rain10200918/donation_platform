package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用于表示系统中的用户信息，包含用户的基本属性和登录相关信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Users {
    /** 用户唯一标识ID */
    private Long id;

    /** 用户类型 */
    private String userType;

    /** 用户昵称 */
    private String nickName;

    /** 手机号码 */
    private String mobile;

    /** 邮箱地址 */
    private String email;

    /** 登录密码 */
    private String password;

    /** 用户头像URL */
    private String avatar;

    /** 用户状态 */
    private String status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 最后登录IP地址 */
    private String lastLoginIp;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 角色权限 */
    private String role;

    /** 余额 */
    private BigDecimal balance;
}

