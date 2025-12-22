package com.example.donationplatform.aspect;

import com.example.donationplatform.entity.Users;
import com.example.donationplatform.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {
    @Before("@annotation(com.example.donationplatform.annotation.AdminOnly)")
    public void checkRole(){
        Users user = UserContext.getUser();
        if (user == null){
            throw  new RuntimeException("用户未登陆");
        }
        if (!user.getRole().equals("1")){
            log.error("用户权限越界");
            throw new RuntimeException("用户无权限");
        }
        log.info("用户权限正常");
    }
}
