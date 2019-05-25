package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.ApplicationConstants;

/**
 * Command which is used to change locale.
 */
public class ChangeLocaleCommand extends Command {

    public ChangeLocaleCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) {
        CommandResult commandResult = new CommandResult();
        String locale = data.getRequestParameter(ApplicationConstants.LOCALE_PARAMETER);
        data.putSessionAttribute(ApplicationConstants.LOCALE_ATTRIBUTE, locale);
        return commandResult;
    }
}
