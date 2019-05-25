package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.PageConstants;

/**
 * Command which is used to move to the registration page.
 */
public class ShowRegistrationPageCommand extends Command {

    public ShowRegistrationPageCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) {
        CommandResult result = new CommandResult();
        result.assignTransitionTypeForward();
        result.setPage(PageConstants.REGISTRATION_PAGE);
        return result;
    }
}
