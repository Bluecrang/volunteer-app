package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.PageConstants;

/**
 * Command that is used to move to the index page.
 */
public class MoveToIndexPageCommand extends Command {

    public MoveToIndexPageCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) {
        CommandResult result = new CommandResult();
        result.assignTransitionTypeForward();
        result.setPage(PageConstants.INDEX_PAGE);
        return result;
    }
}
