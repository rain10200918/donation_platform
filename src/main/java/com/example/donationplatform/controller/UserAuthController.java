package com.example.donationplatform.controller;

import com.example.donationplatform.dto.UserAuthDTO;
import com.example.donationplatform.entity.Result;
import com.example.donationplatform.entity.UserAuth;
import com.example.donationplatform.entity.Users;
import com.example.donationplatform.service.UserAuthService;
import com.example.donationplatform.util.IdCardUtil;
import com.example.donationplatform.util.UserContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-auth")
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;
   @GetMapping("/detail")
    public Result detail(){
       Users user = UserContext.getUser();
       if (user == null){
           return Result.error("用户未登录");
       }
       UserAuth detailById = userAuthService.getDetailById(user.getId());
       return Result.success(detailById);
   }
    @PostMapping("/apply")
    public Result apply(@RequestBody UserAuthDTO authDto) {
        Users user = UserContext.getUser();
        if (user == null) return Result.error("未登录");

        // 1. 格式校验
        if (!IdCardUtil.isValidName(authDto.getRealName())) {
            return Result.error("姓名格式不正确");
        }
        if (!IdCardUtil.isValidIdCard(authDto.getIdNumber())) {
            return Result.error("身份证号格式不正确");
        }

        // 2. 检查是否已有申请记录
        UserAuth existingAuth = userAuthService.getDetailById(user.getId());
        if (existingAuth != null) {
            if (existingAuth.getAuthStatus() == 0) return Result.error("认证审核中，请勿重复提交");
            if (existingAuth.getAuthStatus() == 1) return Result.error("您已通过实名认证");
            // 如果 status 为 2 (被驳回)，则允许重新提交，下面执行更新逻辑
        }

        // 3. 执行保存/更新
        UserAuth auth = (existingAuth != null) ? existingAuth : new UserAuth();
        auth.setUserId(user.getId());
        auth.setRealName(authDto.getRealName());
        auth.setIdNumber(authDto.getIdNumber());
        auth.setAuthStatus(0); // 重置为待审核

        userAuthService.saveOrUpdate(auth);
        return Result.success("申请成功，等待审核");
    }
}
