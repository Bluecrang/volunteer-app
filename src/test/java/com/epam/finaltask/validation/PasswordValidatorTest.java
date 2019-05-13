package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordValidatorTest {

    private PasswordValidator passwordValidator = new PasswordValidator();

    @Test
    public void validateTestValidPassword() {
        String password = "password";

        boolean actual = passwordValidator.validate(password);

        Assert.assertTrue(actual);
    }

    @Test
    public void validateTestPasswordNull() {
        String password = null;

        boolean actual = passwordValidator.validate(password);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestPasswordTooLong() {
        String password = "this string is too long to be password and should not be valid to be a password";

        boolean actual = passwordValidator.validate(password);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestPasswordTooShort() {
        String password = "passw";

        boolean actual = passwordValidator.validate(password);

        Assert.assertFalse(actual);
    }
}
