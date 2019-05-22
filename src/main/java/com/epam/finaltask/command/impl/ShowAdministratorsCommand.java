package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import com.epam.finaltask.validation.AdministratorValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command which initializes data for administrators page.
 */
public class ShowAdministratorsCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACCOUNT_LIST_ATTRIBUTE = "account_list";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            Object accountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
            AdministratorValidator validator = new AdministratorValidator();
            if (validator.validate(accountObject)) {
                AccountService accountService = new AccountService();
                List<Account> accountList = accountService.findAdministrators();
                logger.log(Level.DEBUG, "account list: " + accountList);
                data.putRequestAttribute(ACCOUNT_LIST_ATTRIBUTE, accountList);
                commandResult.setPage(PageConstants.ADMINISTRATORS_PAGE);
            } else {
                commandResult.assignTransitionTypeError();
                commandResult.setCode(ApplicationConstants.PAGE_NOT_FOUND_ERROR_CODE);
            }
        } catch (ServiceException e) {
            throw new CommandException("Unable to show administrators page: ServiceException has occurred", e);
        }
        return commandResult;
    }
}
