package com.epam.finaltask.validation;

import com.epam.finaltask.command.impl.UploadCommandType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Validation class that is used to define if string can be associated with {@link UploadCommandType} constant.
 */
public class UploadCommandValidator {

    /**
     * Checks if string has corresponding enum constant in {@link UploadCommandType}.
     * @param command   upload command name
     * @return {@code true} if command parameter has corresponding enum constant in {@link UploadCommandType}.
     */
    public boolean validate(String command) {
        if (command == null) {
            return false;
        }
        String formattedCommand = command.replaceAll("-", "_").toUpperCase();
        UploadCommandType[] uploadCommandTypes = UploadCommandType.values();
        Optional<UploadCommandType> foundUploadCommand = Arrays.stream(uploadCommandTypes)
                .filter(uploadCommandType -> uploadCommandType.name().equals(formattedCommand)).findFirst();
        return foundUploadCommand.isPresent();
    }
}
