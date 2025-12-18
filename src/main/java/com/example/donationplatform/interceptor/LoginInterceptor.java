package com.example.donationplatform.interceptor;

import com.example.donationplatform.entity.Users;
import com.example.donationplatform.service.UsersService;
import com.example.donationplatform.util.JwtUtil;
import com.example.donationplatform.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UsersService usersService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("进入拦截器");
        log.info("token:{}", request.getHeader("Authorization"));
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()){
            return false;
        }
        try {
            Claims user = JwtUtil.parseToken(token.replace("Bearer ", ""));
            log.info("解析token:{}", user);
            long userId = Long.parseLong(user.getSubject());
            Users users = usersService.selectById(userId);
            UserContext.setUser(users);
            log.info("用户信息:{}", users);
        } catch (Exception e) {
            log.error("解析token失败:{}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        log.info("用户信息:{}", UserContext.getUser());
        log.info("放行");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
