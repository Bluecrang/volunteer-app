package com.epam.finaltask.validation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ImageFilenameValidatorTest {

    private ImageFilenameValidator imageFilenameValidator = new ImageFilenameValidator();

    @DataProvider(name = "InvalidExtensionFilenameProvider")
    public Object[][] provideFilenamesWithInvalidExtensions() {
        return new Object[][] {
                {"file.xml"},
                {"image.jpek"},
                {"img.doc"}
        };
    }

    @DataProvider(name = "ValidFilenameProvider")
    public Object[][] provideValidFilenames() {
        return new Object[][] {
                {"filename.jpeg"},
                {"pic.jpg"},
                {"something.png"},
                {"file134.bmp"},
                {"picture.img.png"},
                {"pi4c.cat.jpeg"},
                {"dog.imag3e.jpg"},
        };
    }

    @Test(dataProvider = "ValidFilenameProvider")
    public void validateTestValidImageFilename(String filename) {
        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertTrue(actual);
    }

    @Test(dataProvider = "InvalidExtensionFilenameProvider")
    public void validateTestInvalidImageFilenameExtension(String filename) {
        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestFilenameNull() {
        String filename = null;

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestFilenameBlank() {
        String filename = "";

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }

    @Test
    public void validateTestNoExtension() {
        String filename = "picture";

        boolean actual = imageFilenameValidator.validate(filename);

        Assert.assertFalse(actual);
    }
}
