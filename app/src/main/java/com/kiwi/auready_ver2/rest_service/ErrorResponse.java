package com.kiwi.auready_ver2.rest_service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kiwi on 6/19/16.
 */
public class ErrorResponse {

    private static final Integer DEFAULT_CODE = 400;
    private static final String DEFAULT_MESSAGE = "System Error: Signup is failed";

    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;


    public ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse() {
        this.code = DEFAULT_CODE;
        this.message = DEFAULT_MESSAGE;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
