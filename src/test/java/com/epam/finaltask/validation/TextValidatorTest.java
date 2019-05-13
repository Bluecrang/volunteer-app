package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TextValidatorTest {

    private TextValidator textValidator = new TextValidator();

    @Test
    public void validateTestTextLengthLessThanMaxLength() {
        String text = "text";
        int maxLength = 5;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertTrue(actual);
    }

    @Test
    public void validateTestTextLengthEqualsMaxLength() {
        String text = "house";
        int maxLength = 5;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertTrue(actual);
    }

    @Test
    public void validateTestTextLengthGreaterThanMaxLength() {
        String text = "machine";
        int maxLength = 3;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestTextNull() {
        String text = "null";
        int maxLength = 3;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestTextBlank() {
        String text = "";
        int maxLength = 0;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }
}
