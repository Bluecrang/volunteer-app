package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command which initializes data for administrators page.
 */
public class ShowAdministratorsCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACCOUNT_LIST_ATTRIBUTE = "account_list";

    public ShowAdministratorsCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            AccountService accountService = new AccountService();
            List<Account> accountList = accountService.findAdministrators();
            logger.log(Level.DEBUG, "account list: " + accountList);
            data.putRequestAttribute(ACCOUNT_LIST_ATTRIBUTE, accountList);
            commandResult.setPage(PageConstants.ADMINISTRATORS_PAGE);
        } catch (ServiceException e) {
            throw new CommandException("Unable to show administrators page: ServiceException has occurred", e);
        }
        return commandResult;
    }
}
