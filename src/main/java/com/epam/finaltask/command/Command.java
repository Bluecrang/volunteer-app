package com.epam.finaltask.command;

public interface Command {
    CommandResult execute(CommandData data) throws CommandException;
}
