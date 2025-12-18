package com.example.donationplatform.controller;

import com.example.donationplatform.dto.UsersRegisterDTO;
import com.example.donationplatform.dto.UsersVO;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.service.UsersService;
import com.example.donationplatform.util.MD5Util;
import com.example.donationplatform.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        password = MD5Util.md5(password);
        String token = usersServiceImpl.selectByMobile(mobile,password);
        usersServiceImpl.updateUserLoginInfo(mobile);
        return Result.success(token);
    }
    @PostMapping("/register")
    public Result register(@RequestBody UsersRegisterDTO usersRegisterDTO){
        try {
            usersServiceImpl.register(usersRegisterDTO);
            return Result.success("注册成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/info")
    public Result info(){
        Users user = UserContext.getUser();
        Users users = usersServiceImpl.getUserById(user.getId());
        return Result.success(users);
    }
}
