package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.ApplicationConstants;

/**
 * Command that is used to log out.
 */
public class LogoutCommand extends Command {

    public LogoutCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) {
        CommandResult result = new CommandResult();
        result.setPage(ApplicationConstants.SHOW_MAIN_PAGE);
        result.raiseSessionInvalidationFlag();
        return result;
    }
}
