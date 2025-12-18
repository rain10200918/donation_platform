package com.example.donationplatform.service;

import com.example.donationplatform.entity.Users;


public interface UsersService {
    String selectByMobile(String mobile,String  password);

    Users selectById(long userId);
}
