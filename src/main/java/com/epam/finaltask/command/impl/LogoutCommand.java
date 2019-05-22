package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.util.ApplicationConstants;

/**
 * Command which is used to log out.
 */
public class LogoutCommand implements Command {

    @Override
    public CommandResult execute(CommandData data) {
        CommandResult result = new CommandResult();
        result.setPage(ApplicationConstants.SHOW_MAIN_PAGE);
        result.raiseSessionInvalidationFlag();
        return result;
    }
}
