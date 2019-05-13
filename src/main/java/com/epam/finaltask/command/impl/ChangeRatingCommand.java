package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChangeRatingCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
            int value = Integer.parseInt(data.getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER));
            Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
            if (sessionAccountObject instanceof Account) {
                Account sessionAccount = (Account) sessionAccountObject;
                try {
                    if (sessionAccount.getAccessLevel() == AccessLevel.ADMIN) {
                        AccountService accountService = new AccountService();
                        if (accountService.addValueToRating(sessionAccount, accountId, value)) {
                            logger.log(Level.INFO, "account id=" + accountId + " rating changed by " + value);
                        } else {
                            logger.log(Level.WARN, "could not change account id=" + accountId + " rating");
                        }
                    }
                } catch (ServiceException e) {
                    throw new CommandException("unable to change account id=" + accountId + " rating", e);
                }
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse parameter", e);
        }
        return commandResult;
    }
}
