package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.CommandValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Factory that is used to create commands.
 */
public class CommandFactory {

    private static final Logger logger = LogManager.getLogger();
    private static final CommandFactory COMMAND_FACTORY = new CommandFactory();

    private CommandFactory() {
    }

    public static CommandFactory getInstance() {
        return COMMAND_FACTORY;
    }

    /**
     * Retrieves command parameter from the request and tries to create command using it.
     * @param request Request containing command parameter
     * @return Created command
     */
    public Command defineCommand(HttpServletRequest request) {
        String commandString = request.getParameter(ApplicationConstants.COMMAND_PARAMETER);
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
