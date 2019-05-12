package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UsernameValidatorTest {

    private UsernameValidator usernameValidator = new UsernameValidator();

    @DataProvider(name = "InvalidUsernameProvider")
    public Object[][] provideInvalidUserNames() {
        return new Object[][] {
                {"ab"},
                {"this_string_is_to"},
                {"cat dog3"},
                {"dollar$"},
                {"ca&t"},
                {"d^og"},
                {"hous4e!"},
                {"ca/t"},
                {null},
                {""}
        };
    }

    @Test
    public void validateTestValidUsername() {
        String username = "fog";

        boolean actual = usernameValidator.validate(username);

        Assert.assertTrue(actual);
    }

    @Test(dataProvider = "InvalidUsernameProvider")
    public void validateTestInvalidUsername(String username) {
        boolean actual = usernameValidator.validate(username);

        Assert.assertFalse(actual);
    }
}
