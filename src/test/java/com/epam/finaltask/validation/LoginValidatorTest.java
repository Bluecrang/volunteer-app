package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginValidatorTest {

    LoginValidator loginValidator = new LoginValidator();

    @DataProvider(name = "InvalidLoginProvider")
    public Object[][] provideInvalidLogins() {
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
    public void validateTestValidLogin() {
        String login = "fog";

        boolean actual = loginValidator.validate(login);

        Assert.assertTrue(actual);
    }

    @Test(dataProvider = "InvalidLoginProvider")
    public void validateTestInvalidLogin(String login) {
        boolean actual = loginValidator.validate(login);

        Assert.assertFalse(actual);
    }
}
