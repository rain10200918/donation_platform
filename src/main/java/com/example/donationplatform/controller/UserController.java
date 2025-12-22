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

import java.math.BigDecimal;
import java.util.Map;

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
    @PostMapping("/update")
    public Result update(@RequestBody Users  users ){
     return null;
    }
    @PostMapping("/recharge")
    public Result recharge(@RequestBody Map<String, Object> params){
        Long userId = UserContext.getUser().getId();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("充值金额必须大于0");
        }
        usersServiceImpl.recharge(userId, amount);
        return Result.success("充值成功");
    }
}
