package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.AdministratorValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Part;

/**
 * Command which is used to promote user to administrator.
 */
public class PromoteUserToAdminCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String PROMOTION_ERROR = "profile.promotion_error";
    private static final String ACCOUNT_PROMOTED = "profile.account_promoted";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult commandResult = new CommandResult();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
            AdministratorValidator validator = new AdministratorValidator();
            if (validator.validate(sessionAccountObject)) {
                try {
                    AccountService accountService = new AccountService();
                    if (accountService.promoteUserToAdmin(accountId)) {
                        data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, ACCOUNT_PROMOTED);
                        logger.log(Level.INFO, "account id=" + accountId + " promoted to admin");
                    } else {
                        data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, PROMOTION_ERROR);
                        logger.log(Level.WARN, "could not promote account id="+ accountId + " to admin");
                    }
                } catch (ServiceException e) {
                    throw new CommandException("unable to promote account to admin", e);
                }
            } else {
                data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, PROMOTION_ERROR);
                logger.log(Level.WARN, "could not promote account id="+ accountId + " to admin. " +
                        "Promoting account has not enough rights");
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
