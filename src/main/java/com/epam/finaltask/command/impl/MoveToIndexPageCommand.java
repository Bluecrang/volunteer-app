package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.PageConstants;

public class MoveToIndexPageCommand implements Command {
    @Override
    public CommandResult execute(RequestData data) {
        CommandResult result = new CommandResult();
        result.setPage(PageConstants.INDEX_PAGE);
        return result;
    }
}
