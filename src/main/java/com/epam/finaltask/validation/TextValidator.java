package com.epam.finaltask.validation;

import org.apache.commons.lang3.StringUtils;

/**
 * Validation class. Provides validation logic to check if string meets specific conditions.
 */
public class TextValidator {

    /**
     * Checks if text does not exceed specified maximal length.
     * @param text      string to be validated
     * @param maxLength maximal length of the string to be valid
     * @return {@code true} if text does not exceed maximal length. Returns {@code false} if text is null or blank
     */
    public boolean validate(String text, int maxLength) {
        boolean result = false;
        if (StringUtils.isNotBlank(text)) {
            if (text.length() <= maxLength) {
                result = true;
            }
        }
        return result;
    }
}