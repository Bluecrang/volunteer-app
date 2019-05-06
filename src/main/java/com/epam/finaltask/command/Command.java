package com.epam.finaltask.command;

import com.epam.finaltask.command.impl.CommandException;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.RequestData;

public interface Command {

    CommandResult execute(RequestData data) throws CommandException;
}
