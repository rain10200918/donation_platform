package com.example.donationplatform.service;

import com.example.donationplatform.dto.StatusDTO;
import com.example.donationplatform.dto.UsersRegisterDTO;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.entity.Users;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;


public interface UsersService {
    String selectByMobile(String mobile,String  password);

    Users selectById(long userId);

    void updateUserLoginInfo(String mobile);

    void register(UsersRegisterDTO usersRegisterDTO);

    Users getUserById(Long id);

    PageInfo<Users> getUserList(Integer pageNum, Integer pageSize, String nickName, String status);

    void updateUserStatus(StatusDTO dto);

    void recharge(Long userId, BigDecimal amount);
}
