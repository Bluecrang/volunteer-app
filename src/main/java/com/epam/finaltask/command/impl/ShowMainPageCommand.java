package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.util.PageConstants;

public class ShowMainPageCommand implements Command {

    @Override
    public CommandResult execute(CommandData data) {
        CommandResult result = new CommandResult();
        result.assignTransitionTypeForward();
        result.setPage(PageConstants.MAIN_PAGE);
        return result;
    }
}
