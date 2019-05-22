package com.epam.finaltask.command;

/**
 * Command interface. Implementations of this interface are used to perform different actions.
 */
public interface Command {

    /**
     * Executes command using chosen data.
     * @param data Data for command execution
     * @return CommandResult, which contains page and transition type
     * @throws CommandException If command execution failed
     */
    CommandResult execute(CommandData data) throws CommandException;
}
