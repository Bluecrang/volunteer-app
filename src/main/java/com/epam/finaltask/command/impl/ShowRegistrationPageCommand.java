package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.util.PageConstants;

/**
 * Command which is used to move to the registration page.
 */
public class ShowRegistrationPageCommand implements Command {

    @Override
    public CommandResult execute(CommandData data) {
        CommandResult result = new CommandResult();
        result.assignTransitionTypeForward();
        result.setPage(PageConstants.REGISTRATION_PAGE);
        return result;
    }
}
