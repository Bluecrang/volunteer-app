package com.epam.finaltask.validation;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EmailValidatorTest {

    private EmailValidator emailValidator = new EmailValidator();

    @Test
    public void validate_validEmail_true() {
        String email = "email@mail.com";

        boolean actual = emailValidator.validate(email);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_emailLength255_false() {
        String email = StringUtils.repeat('b', 246) + "@mail.com";

        boolean actual = emailValidator.validate(email);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_emailLength254_false() {
        String email = StringUtils.repeat('b', 245) + "@mail.com";

        boolean actual = emailValidator.validate(email);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_emailLength253_true() {
        String email = StringUtils.repeat('b', 244) + "@mail.com";

        boolean actual = emailValidator.validate(email);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_invalidEmail_false() {
        String email = "r8";

        boolean actual = emailValidator.validate(email);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_emailNull_false() {
        String email = null;

        boolean actual = emailValidator.validate(email);

        Assert.assertFalse(actual);
    }
}