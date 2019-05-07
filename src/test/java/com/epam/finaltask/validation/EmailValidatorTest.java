package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EmailValidatorTest {

    EmailValidator emailValidator = new EmailValidator();

    @DataProvider(name = "ValidEmailProvider")
    public static Object[][] provideValidEmails() {
        return new Object[][] {
                {"vasya@gmail.com"},
                {"rij1i_1@mail.ru"},
                {"e_ae@gma.co"},
                {"ei13g3@fff.ru"},
                {"eag__e1t.egw.qwe_1@gmial.co.ae"},
                {"qerfe.aeqwe@ger.com"},
                {"ae_aet_er.egg@aer.aeaer.aereq.caer"},
                {"dog1.cat@outlook.com"},
        };
    }

    @DataProvider(name = "InvalidEmailProvider")
    public Object[][] provideInvalidEmails() {
        return new Object[][] {
                {"va@gmail.com"},
                {"house@@ee.ru"},
                {"fish@mail."},
                {"former@.ru"},
                {"@mail.com"},
                {"cat..dog@outlook.com"},
                {"a.e@mail.ce"},
                {"rock@ma_il.com"},
                {"qijeqe.qefqef._qefqefqfef@gamail.com"},
                {"cat@gmail.com.co.ce.cu.hu"},
                {"dog@com"},
                {null},
                {""}
        };
    }

    @Test(dataProvider = "ValidEmailProvider")
    public void validateTestValidEmail(String email) {
        boolean actual = emailValidator.validate(email);
        Assert.assertTrue(actual);
    }

    @Test(dataProvider = "InvalidEmailProvider")
    public void validateTestInvalidEmail(String email) {
        boolean actual = emailValidator.validate(email);
        Assert.assertFalse(actual);
    }
}
