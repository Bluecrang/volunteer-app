package com.epam.finaltask.command;

import com.epam.finaltask.command.impl.CommandConstraints;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.HttpMethodType;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.validation.CommandDataValidator;

import javax.servlet.http.Part;
import java.util.Collection;

/**
 * Class, instances of which are used to upload files to server.
 */
public abstract class UploadCommand {

    /**
     * Constraints which specify what type of account can execute command and which http method can be used.
     */
    private CommandConstraints constraints;

    /**
     * Constructor which assigns chosen constraints or uses default if chosen constraints is {@code null}.
     * @param constraints Constraints to be applied to command.
     */
    public UploadCommand(CommandConstraints constraints) {
        if (constraints != null) {
            this.constraints = constraints;
        } else {
            this.constraints = CommandConstraints.builder()
                    .buildHttpMethods(HttpMethodType.POST)
                    .buildAccountTypes(AccountType.USER)
                    .build();
        }
    }

    /**
     * Template method, executes command using chosen data and parts.
     * @param data Data to be used by command
     * @param parts Parts to be used by command
     * @return Result of the execution as {@link CommandResult} instance
     * @throws CommandException If upload command can't be executed
     */
    public CommandResult execute(CommandData data, Collection<Part> parts) throws CommandException {
        CommandDataValidator validator = new CommandDataValidator();
        if (validator.validate(data, constraints)) {
            return performAction(data, parts);
        } else {
            throw new CommandException("Could not validate command against constraints");
        }
    }

    /**
     * Method which is used in {@link UploadCommand#execute(CommandData, Collection)}. Defines what command should do.
     * @param data Data to be used to perform action
     * @param parts Parts to be used to perform action
     * @return Result of the execution as {@link CommandResult} instance
     * @throws CommandException If action can't be performed
     */
    protected abstract CommandResult performAction(CommandData data, Collection<Part> parts) throws CommandException;
}
