package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ImageFilenameValidatorTest {

    private ImageFilenameValidator imageFilenameValidator = new ImageFilenameValidator();

    @Test
    public void validate_filenameBlank_false() {
        String filename = "";

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_validImageFilename_true() {
        String filename = "abc.jpg";

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertTrue(actual);
    }

    @Test
    public void validate_invalidImageExtension_false() {
        String filename = "abc.zip";

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_noExtension_false() {
        String filename = "abcd";

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_filenameNull_false() {
        String filename = null;

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }
}