package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.validation.CommandValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class CommandFactory {

    private static final Logger logger = LogManager.getLogger();
    private static final String COMMAND_PARAMETER = "command";

    public Command defineCommand(HttpServletRequest request) {
        String commandString = request.getParameter(COMMAND_PARAMETER);
        CommandValidator commandValidator = new CommandValidator();
        if (commandValidator.validate(commandString)) {
            logger.log(Level.INFO, "command commandString=" + commandString + " found in command types");
            CommandType commandType = CommandType.valueOf(commandString.replaceAll("-", "_").toUpperCase());
            return commandType.getCommand();
        }
        logger.log(Level.INFO, "unable to define command commandString=" + commandString + ", move_to_index command will be used");
        return CommandType.MOVE_TO_INDEX_PAGE.getCommand();
    }
}
