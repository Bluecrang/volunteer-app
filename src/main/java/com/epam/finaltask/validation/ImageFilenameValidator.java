package com.epam.finaltask.validation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageFilenameValidator {

    private static final Logger logger = LogManager.getLogger();

    private static final String[] extensions = {"jpeg", "jpg", "png", "bmp"};
    private static final char FILE_EXTENSION_SEPARATOR = '.';

    public boolean validate(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            int lastDotIndex = fileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            if (lastDotIndex != -1) {
                String extension = fileName.substring(lastDotIndex + 1);
                for (String validExtension : extensions) {
                    if (extension.equals(validExtension)) {
                        logger.log(Level.INFO, "filename " + fileName + " successfully validated");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
