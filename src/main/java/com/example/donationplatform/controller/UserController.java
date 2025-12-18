package com.example.donationplatform.controller;

import com.example.donationplatform.dto.UsersVO;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersServiceImpl;
    @PostMapping("/login")
    public Result login(@RequestBody UsersVO usersVO){
        String mobile = usersVO.getMobile();
        String password = usersVO.getPassword();
        if (mobile == null){
            return Result.error("手机号码不能为空");
        }
        String token = usersServiceImpl.selectByMobile(mobile,password);
        return Result.success(token);
    }
}
