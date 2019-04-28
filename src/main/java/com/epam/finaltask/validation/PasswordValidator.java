package com.epam.finaltask.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_REGEX = "^.{6,64}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public boolean validate(String password) {
        boolean result = false;
        if (password != null) {
            Matcher matcher = PASSWORD_PATTERN.matcher(password);
            if (matcher.matches()) {
                result = true;
            }
        }
        return result;
    }
}
