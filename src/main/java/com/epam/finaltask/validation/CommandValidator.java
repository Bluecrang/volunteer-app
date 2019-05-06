package com.epam.finaltask.validation;

import com.epam.finaltask.command.impl.CommandType;

import java.util.Arrays;
import java.util.Optional;

public class CommandValidator {

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
