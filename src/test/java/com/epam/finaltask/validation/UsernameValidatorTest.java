package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UsernameValidatorTest {

    private UsernameValidator usernameValidator = new UsernameValidator();

    @Test
    public void validate_usernameContainsSpecialSymbol_false() {
        String username = "abcfeq&";

        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_usernameTooShort_false() {
        String username = "a";

        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_usernameTooLong_false() {
        String username = "aqjfiqjefijqfqoneqfquvbqeuqovnqievqibveuqoveaibevuqovbieoanviebqevqe";

        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_username17CharactersLong_false() {
        String username = "abcabcabcabcabcac";

        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_username16CharactersLong_true() {
        String username = "abcabcabcabcabca";

        boolean actual = usernameValidator.validate(username);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_username3CharactersLong_true() {
        String username = "abc";

        boolean actual = usernameValidator.validate(username);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_username2CharactersLong_false() {
        String username = "ab";

        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_validUsername_true() {
        String username = "abcdefg13";

        boolean actual = usernameValidator.validate(username);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_usernameNull_false() {
        String username = null;

        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }
}