package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.util.ApplicationConstants;

public class ChangeLocaleCommand implements Command {

    @Override
    public CommandResult execute(CommandData data) {
        CommandResult commandResult = new CommandResult();
        String locale = data.getRequestParameter(ApplicationConstants.LOCALE_PARAMETER);
        data.putSessionAttribute(ApplicationConstants.LOCALE_ATTRIBUTE, locale);
        return commandResult;
    }
}
