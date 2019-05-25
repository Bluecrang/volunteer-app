package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command which is used to promote user to administrator.
 */
public class PromoteUserToAdminCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String PROMOTION_ERROR = "profile.promotion_error";
    private static final String ACCOUNT_PROMOTED = "profile.account_promoted";

    public PromoteUserToAdminCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
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
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
