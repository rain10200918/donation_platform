package com.example.donationplatform.service;

import com.example.donationplatform.entity.UserAuth;

public interface UserAuthService {
    UserAuth getDetailById(Long id);

    UserAuth getByrealNameAndIdNumber(String realName, String idNumber);

    void saveOrUpdate(UserAuth auth);
}
