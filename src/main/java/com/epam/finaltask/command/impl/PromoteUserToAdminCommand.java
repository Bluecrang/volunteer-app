package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PromoteUserToAdminCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
            if (sessionAccountObject instanceof Account) {
                try {
                    AccountService accountService = new AccountService();
                    Account sessionAccount = (Account) sessionAccountObject;
                    if (accountService.promoteUserToAdmin(sessionAccount, accountId)) {
                        logger.log(Level.INFO, "account id=" + accountId + " promoted to admin");
                    } else {
                        logger.log(Level.WARN, "could not promote account id="+ accountId + " to admin");
                    }
                } catch (ServiceException e) {
                    throw new CommandException("unable to promote account to admin", e);
                }
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
