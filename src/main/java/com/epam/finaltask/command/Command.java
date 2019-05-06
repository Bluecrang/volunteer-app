package com.epam.finaltask.command;

import com.epam.finaltask.command.impl.CommandException;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.CommandData;

public interface Command {

    CommandResult execute(CommandData data) throws CommandException;
}
