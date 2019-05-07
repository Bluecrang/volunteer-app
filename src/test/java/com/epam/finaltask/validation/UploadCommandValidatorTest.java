package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UploadCommandValidatorTest {

    UploadCommandValidator uploadCommandValidator = new UploadCommandValidator();

    @Test
    public void validateTestValidUploadCommand() {
        String command = "upload_avatar";

        boolean actual = uploadCommandValidator.validate(command);

        Assert.assertTrue(actual);
    }

    @Test
    public void validateTestUploadCommandNull() {
        String command = null;

        boolean actual = uploadCommandValidator.validate(command);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestNonexistentUploadCommand() {
        String command = "nonexistent_command";

        boolean actual = uploadCommandValidator.validate(command);

        Assert.assertFalse(actual);
    }
}
