package com.kiwi.auready.util;

import java.util.regex.Pattern;

/**
 * Created by kiwi on 6/18/16.
 */
public interface LoginUtils {

    boolean LOGIN = true;
    boolean LOGOUT = false;
    String IS_SUCCESS = "isSuccess";

    String EMAIL_TOKEN = "@";

    // 이메일정규식
    Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    //비밀번호정규식
    Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$"); // 4자리 ~ 16자리까지 가능
//
//    public static boolean validatePassword(String pwStr) {
//        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
//        return matcher.matches();
//    }
}
