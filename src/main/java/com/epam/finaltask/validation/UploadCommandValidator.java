package com.epam.finaltask.validation;

import com.epam.finaltask.command.impl.UploadCommandType;

import java.util.Arrays;
import java.util.Optional;

public class UploadCommandValidator {

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
