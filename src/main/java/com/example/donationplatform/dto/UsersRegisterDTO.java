package com.example.donationplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersRegisterDTO {

    private String nickName;
    private String mobile;
    private String email;
    private String password;
    private String avatar; // 可选
}
