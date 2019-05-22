package com.epam.finaltask.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation class which is used to check if password can be used for registration.
 */
public class UsernameValidator {

    /**
     * Regular expression used to validate username.
     */
    private static final String USERNAME_REGEX = "^\\w{3,16}$";

    /**
     * Username validation pattern.
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    /**
     * Checks if username is valid using regular expression.
     * @param username username to be validated
     * @return {@code true} if username contains only [A-Za-z0-9_] characters and its length is between 3 and 16 characters.
     * Returns {@code false} if username is null
     */
    public boolean validate(String username) {
        boolean result = false;
        if (username != null) {
            Matcher matcher = USERNAME_PATTERN.matcher(username);
            if (matcher.matches()) {
                result = true;
            }
        }
        return result;
    }
}
