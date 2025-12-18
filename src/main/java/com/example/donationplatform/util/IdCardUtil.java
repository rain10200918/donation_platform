package com.example.donationplatform.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class IdCardUtil {

    /**
     * 校验姓名（中文 2~10 位）
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        return name.matches("^[\\u4e00-\\u9fa5]{2,10}$");
    }

    /**
     * 校验身份证号是否合法
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) return false;

        // 前17位必须是数字
        String num17 = idCard.substring(0, 17);
        if (!num17.matches("\\d+")) return false;

        // 校验生日是否合法
        String birth = idCard.substring(6, 14);
        if (!isValidDate(birth)) return false;

        // 校验码校验
        return checkCode(idCard);
    }

    /**
     * 校验身份证校验码
     */
    private static boolean checkCode(String idCard) {
        char[] chars = idCard.substring(0, 17).toCharArray();
        char last = idCard.charAt(17);

        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] validate = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < weight.length; i++) {
            sum += (chars[i] - '0') * weight[i];
        }

        char code = validate[sum % 11];
        return code == last;
    }

    /**
     * 校验日期是否合法（yyyyMMdd）
     */
    private static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 从身份证解析性别（1 男，2 女）
     */
    public static int getGender(String idCard) {
        int genderNum = idCard.charAt(16) - '0';
        return genderNum % 2 == 0 ? 2 : 1;
    }

    /**
     * 对外统一校验方法
     */
    public static boolean validate(String realName, String idCard) {
        return isValidName(realName) && isValidIdCard(idCard);
    }
}
