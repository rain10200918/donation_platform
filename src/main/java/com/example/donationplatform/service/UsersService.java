package com.example.donationplatform.service;

import com.example.donationplatform.dto.UsersRegisterDTO;
import com.example.donationplatform.entity.Users;


public interface UsersService {
    String selectByMobile(String mobile,String  password);

    Users selectById(long userId);

    void updateUserLoginInfo(String mobile);

    void register(UsersRegisterDTO usersRegisterDTO);

    Users getUserById(Long id);
}
