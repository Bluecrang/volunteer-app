package com.epam.finaltask.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation class that is used to check if password can be used for registration.
 */
public class PasswordValidator {

    /**
     * Regular expression used to validate password.
     */
    private static final String PASSWORD_REGEX = "^.{6,64}$";

    /**
     * Password validation pattern.
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * Checks if string can be used as password.
     * @param password string to be validated
     * @return {@code true} if password length is between 6 and 64 characters. Returns {@code false} if password is null
     */
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
