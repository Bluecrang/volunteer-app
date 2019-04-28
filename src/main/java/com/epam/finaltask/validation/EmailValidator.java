package com.epam.finaltask.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_REGEX = "^[\\w!#$%'*+\\-/=?^_`{|}~]{3,15}([.][\\w!#$%'*+\\-/=?^_`{|}~]{3,10}){0,5}@[a-z]{2,20}([.][a-z]{2,10}){1,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

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
