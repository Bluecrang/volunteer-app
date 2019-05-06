package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;

public class LogoutCommand implements Command { //TODO invalidate session

    @Override
    public CommandResult execute(CommandData data) {
        data.putSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE, null);
        CommandResult result = new CommandResult();
        result.setPage(PageConstants.MAIN_PAGE);
        return result;
    }
}
