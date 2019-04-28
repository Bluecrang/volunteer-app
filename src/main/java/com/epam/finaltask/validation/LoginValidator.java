package com.epam.finaltask.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginValidator {

    private static final String LOGIN_REGEX = "^\\w{3,16}$";
    private static final Pattern LOGIN_PATTERN = Pattern.compile(LOGIN_REGEX);

    public boolean validate(String login) {
        boolean result = false;
        if (login != null) {
            Matcher matcher = LOGIN_PATTERN.matcher(login);
            if (matcher.matches()) {
                result = true;
            }
        }
        return result;
    }
}
