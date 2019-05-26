package com.epam.finaltask.command;

import com.epam.finaltask.command.impl.CommandConstraints;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.HttpMethodType;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.validation.CommandDataValidator;

/**
 * Command interface. Implementations of this interface are used to perform different actions.
 */
public abstract class Command {

    /**
     * Constraints that specify what type of account can execute command and which http method can be used.
     */
    private CommandConstraints constraints;

    /**
     * Constructor that assigns chosen constraints or uses default if chosen constraints is {@code null}.
     * @param constraints Constraints to be applied to command.
     */
    public Command(CommandConstraints constraints) {
        if (constraints != null) {
            this.constraints = constraints;
        } else {
            this.constraints = CommandConstraints.builder()
                    .buildHttpMethods(HttpMethodType.GET)
                    .buildAccountTypes(AccountType.GUEST)
                    .build();
        }
    }

    /**
     * Template method, executes command using chosen data.
     * @param data Data for command execution
     * @return CommandResult, that contains page to move to and transition type
     * @throws CommandException If command execution failed
     */
    public CommandResult execute(CommandData data) throws CommandException {
        CommandDataValidator validator = new CommandDataValidator();
        if (validator.validate(data, constraints)) {
            return performAction(data);
        } else {
            throw new CommandException("Could not validate command against constraints");
        }
    }

    /**
     * Method that is used in {@link Command#execute(CommandData)}. Defines what command should do.
     * @param data Data that is used to perform action
     * @return Result of the command as {@link CommandResult} instance.
     * @throws CommandException if action can't be performed.
     */
    protected abstract CommandResult performAction(CommandData data) throws CommandException;
}
