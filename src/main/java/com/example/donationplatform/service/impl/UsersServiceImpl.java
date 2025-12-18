package com.example.donationplatform.service.impl;

import com.example.donationplatform.entity.Users;
import com.example.donationplatform.mapper.UsersMapper;
import com.example.donationplatform.service.UsersService;
import com.example.donationplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;
    @Override
    public String selectByMobile(String mobile,String  password) {
        Users users = usersMapper.selectByMobile(mobile);
        if (!password.equals(users.getPassword())){
           throw new RuntimeException("密码错误");
        }
        return JwtUtil.generateToken(users.getId(),users.getNickName());
    }

    @Override
    public Users selectById(long userId) {
        return usersMapper.selectById(userId);
    }
}
