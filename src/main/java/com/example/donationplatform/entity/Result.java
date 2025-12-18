package com.example.donationplatform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Result {
    private String code;
    private String message;
    private Object data;

    public static Result success(Object data) {
        return new Result("200", "success", data);
    }
    public static Result error(String message) {
        return new Result("500", message, null);
    }
    public static Result error(String code, String message) {
        return new Result(code, message, null);
    }

}
