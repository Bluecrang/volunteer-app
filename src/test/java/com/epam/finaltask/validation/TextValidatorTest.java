package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TextValidatorTest {

    private TextValidator textValidator = new TextValidator();

    @Test
    public void validate_textLengthLessThanMaxLength_true() {
        String text = "abc";
        int maxLength = 5;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_textLengthEqualsMaxLength_true() {
        String text = "house";
        int maxLength = 5;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_textBlankMaxLengthZero_false() {
        String text = "";
        int maxLength = 0;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_textLengthGreaterThanMaxLengthBy1_false() {
        String text = "ab";
        int maxLength = 1;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_textLengthGreaterThanMaxLengthBy13_false() {
        String text = "abcabcabcabcabc";
        int maxLength = 2;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_textNull_false() {
        String text = null;
        int maxLength = 3;

        boolean actual = textValidator.validate(text, maxLength);

        Assert.assertFalse(actual);
    }
}
