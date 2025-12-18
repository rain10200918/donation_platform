package com.example.donationplatform.util;

public class ValidatorUtils {
    // 手机号正则
    private static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";

    // 邮箱正则
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

    // 校验手机号
    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        return mobile.matches(MOBILE_REGEX);
    }

    // 校验邮箱
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches(EMAIL_REGEX);
    }
}
