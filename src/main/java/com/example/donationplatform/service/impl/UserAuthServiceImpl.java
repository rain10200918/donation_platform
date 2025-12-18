package com.example.donationplatform.service.impl;

import com.example.donationplatform.entity.UserAuth;
import com.example.donationplatform.mapper.UserAuthMapper;
import com.example.donationplatform.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Override
    public UserAuth getDetailById(Long id) {
        return userAuthMapper.getById(id);
    }

    @Override
    public UserAuth getByrealNameAndIdNumber(String realName, String idNumber) {
        UserAuth userAuth = userAuthMapper.getByrealNameAndIdNumber(realName, idNumber);
        return userAuth;
    }

    @Override
    public void saveOrUpdate(UserAuth auth) {
        UserAuth existing = userAuthMapper.getById(auth.getUserId());
        if (existing == null) {
            // 2. 情况 A：没有记录，执行插入
            auth.setAuthStatus(0); // 设置初始状态为待审核
            userAuthMapper.insert(auth);
        } else {
            // 3. 情况 B：已有记录（可能是之前被驳回了）
            // 更新关键字段，并重置状态为待审核
            existing.setRealName(auth.getRealName());
            existing.setIdNumber(auth.getIdNumber());
            existing.setAuthStatus(0);
            // 如果你使用了 reject_reason 字段，重新提交时建议清空它
            existing.setRejectReason(null);
            userAuthMapper.updateById(existing);
        }
    }
}
