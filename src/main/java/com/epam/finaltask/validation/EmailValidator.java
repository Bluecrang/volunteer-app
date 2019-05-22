package com.epam.finaltask.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to validate emails. Uses regular expressions for validation.
 */
public class EmailValidator {

    /**
     * Email regular expression
     */
    private static final String EMAIL_REGEX = "^[\\w!#$%'*+\\-/=?^_`{|}~]{3,15}([.][\\w!#$%'*+\\-/=?^_`{|}~]{3,10}){0,5}@[a-z]{2,20}([.][a-z]{2,10}){1,4}$";

    /**
     * {@link Pattern} used for validation.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Checks if email matches the email pattern.
     * @param email string to validate
     * @return {@code true} if email matches pattern. If email is {@code null}, returns {@code false}
     */
    public boolean validate(String email) {
        boolean result = false;
        if (email != null) {
            Matcher matcher = EMAIL_PATTERN.matcher(email);
            if (matcher.matches()) {
                result = true;
            }
        }
        return result;
    }
}
