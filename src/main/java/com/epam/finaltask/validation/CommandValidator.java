package com.epam.finaltask.validation;

import com.epam.finaltask.command.impl.CommandType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Validation class which is used to define if string can be associated with {@link CommandType} constant.
 */
public class CommandValidator {

    /**
     * Checks if string has corresponding enum constant in {@link CommandType}.
     * @param command   command name
     * @return {@code true} if command parameter has corresponding enum constant in {@link CommandType}.
     */
    public boolean validate(String command) {
        if (command == null) {
            return false;
        }
        String formattedCommand = command.replaceAll("-", "_").toUpperCase();
        CommandType[] commandTypes = CommandType.values();
        Optional<CommandType> foundCommand = Arrays.stream(commandTypes)
                .filter(commandType -> commandType.name().equals(formattedCommand)).findFirst();
        return foundCommand.isPresent();
    }
}
