package com.example.donationplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.donationplatform.dto.UsersRegisterDTO;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.mapper.UsersMapper;
import com.example.donationplatform.service.UsersService;
import com.example.donationplatform.util.JwtUtil;
import com.example.donationplatform.util.MD5Util;
import com.example.donationplatform.util.ValidatorUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.donationplatform.constant.Constant.PERSON_TYPE;
import static com.example.donationplatform.constant.Constant.USER_STATUS_NORMAL;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private HttpServletRequest request;
    @Override
    public String selectByMobile(String mobile,String  password) {
        Users users = usersMapper.selectByMobile(mobile);
        if (!password.equals(users.getPassword())){
           throw new RuntimeException("密码错误");
        }

        return JwtUtil.generateToken(users.getId(), users.getNickName());
    }

    @Override
    public Users selectById(long userId) {
        return usersMapper.selectById(userId);
    }

    @Override
    public void updateUserLoginInfo(String mobile) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        Users user = new Users();
        user.setLastLoginIp(ip);
        user.setLastLoginAt(LocalDateTime.now());
        user.setMobile( mobile);
        user.setUpdateTime(LocalDateTime.now());
        usersMapper.updateUserLoginInfo( user);
    }

    @Override
    public void register(UsersRegisterDTO usersRegisterDTO) {
        if (!ValidatorUtils.isValidMobile(usersRegisterDTO.getMobile())){
            throw new IllegalArgumentException("手机号格式不正确");
        }
        if (!ValidatorUtils.isValidEmail(usersRegisterDTO.getEmail())){
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        Users isExist = usersMapper.selectByMobile(usersRegisterDTO.getMobile());
        if (isExist != null){
            throw new IllegalArgumentException("手机号已存在");
        }
        Users emailExist = usersMapper.selectByEmail(usersRegisterDTO.getEmail());
        if (emailExist != null){
            throw new IllegalArgumentException("邮箱已存在");
        }
        String password = MD5Util.md5(usersRegisterDTO.getPassword());
        Users users = new Users();
        BeanUtil.copyProperties(usersRegisterDTO,users);
        users.setPassword(password);
        users.setStatus(USER_STATUS_NORMAL);
        users.setUserType(PERSON_TYPE);
        users.setCreateTime(LocalDateTime.now());
        usersMapper.registerUser(users);
    }

    @Override
    public Users getUserById(Long id) {
        Users users = usersMapper.getUserById(id);
        return users;
    }
}
