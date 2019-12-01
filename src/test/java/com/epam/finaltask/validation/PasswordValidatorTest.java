package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordValidatorTest {

    private PasswordValidator passwordValidator = new PasswordValidator();

    @Test
    public void validate_validPassword_true() {
        String password = "password";

        boolean actual = passwordValidator.validate(password);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_passwordNull_false() {
        String password = null;

        boolean actual = passwordValidator.validate(password);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_passwordTooLong_false() {
        String password = "this string is too long to be password and should not be valid to be a password";

        boolean actual = passwordValidator.validate(password);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_passwordTooShort_false() {
        String password = "passw";

        boolean actual = passwordValidator.validate(password);

        Assert.assertFalse(actual);
    }
}
