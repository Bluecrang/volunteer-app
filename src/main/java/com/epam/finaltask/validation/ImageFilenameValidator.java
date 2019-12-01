package com.epam.finaltask.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Validation class. Provides way to check if string is image filename.
 */
public class ImageFilenameValidator {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Supported extensions: .jpeg, .jpg, .png, .bmp
     */
    private static final List<String> extensions = Arrays.asList("jpeg", "jpg", "png", "bmp");
    private static final char FILE_EXTENSION_SEPARATOR = '.';

    /**
     * Checks if filename is the name of the file with image extension.
     * @param filename image file name to be validated
     * @return {@code true} if filename is image file name. Returns {@code null} if filename is null or empty
     */
    public boolean validate(String filename) {
        boolean result = false;
        if (StringUtils.isNotEmpty(filename)) {
            int lastDotIndex = filename.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            if (lastDotIndex != -1) {
                String extension = filename.substring(lastDotIndex + 1);
                if (extensions.contains(extension)) {
                    logger.log(Level.INFO, "filename " + filename + " is valid");
                    result = true;
                }
            }
        }
        return result;
    }
}
