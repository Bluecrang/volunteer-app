package com.epam.finaltask.validation;

import org.apache.commons.lang3.StringUtils;

public class TextValidator {

    public boolean validate(String text, int maxLength) {
        return (text != null && !StringUtils.isBlank(text) && text.length() <= maxLength);
    }
}
