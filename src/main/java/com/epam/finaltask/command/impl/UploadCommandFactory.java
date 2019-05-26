package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.UploadCommand;
import com.epam.finaltask.validation.UploadCommandValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Factory that is used to create upload commands.
 */
public class UploadCommandFactory {

    private static final Logger logger = LogManager.getLogger();
    private static final String COMMAND_PARAMETER = "command";

    /**
     * Creates UploadCommand using data from the request.
     * @param request Request that is used to create UploadCommand
     * @return Defined UploadCommand
     * @throws CommandException if command creation failed
     */
    public UploadCommand defineCommand(HttpServletRequest request) throws CommandException {
        String commandString = request.getParameter(COMMAND_PARAMETER);
        UploadCommandValidator commandValidator = new UploadCommandValidator();
        if (commandValidator.validate(commandString)) {
            logger.log(Level.INFO, "command commandString=" + commandString + " found in command types");
            UploadCommandType uploadCommandType = UploadCommandType.valueOf(commandString.replaceAll("-", "_").toUpperCase());
            return uploadCommandType.getUploadCommand();
        }
        throw new CommandException("command string " + commandString + " is invalid");
    }
}
