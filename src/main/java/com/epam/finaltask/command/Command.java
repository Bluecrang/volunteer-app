package com.epam.finaltask.command;

import com.epam.finaltask.command.impl.RequestData;
import com.epam.finaltask.command.impl.CommandResult;

public interface Command {

    CommandResult execute(RequestData data);
}
