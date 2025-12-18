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
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()){
            return false;
        }
        try {
            Claims user = JwtUtil.parseToken(token.replace("Bearer ", ""));
            long userId = Long.parseLong(user.getSubject());
            Users users = usersService.selectById(userId);
            UserContext.setUser(users);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
