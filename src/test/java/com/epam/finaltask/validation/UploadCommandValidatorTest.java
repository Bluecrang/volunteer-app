package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class UploadCommandValidatorTest {

    private UploadCommandValidator uploadCommandValidator = new UploadCommandValidator();

    @Test
    public void validate_validUploadCommand_true() {
        String command = "upload_avatar";

        boolean actual = uploadCommandValidator.validate(command);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_uploadCommandNull_false() {
        String command = null;

        boolean actual = uploadCommandValidator.validate(command);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_nonexistentUploadCommand_false() {
        String command = "nonexistent_command";

        boolean actual = uploadCommandValidator.validate(command);

        Assert.assertFalse(actual);
    }
}
