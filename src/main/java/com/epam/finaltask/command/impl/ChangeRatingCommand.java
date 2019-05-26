package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that is used to change rating.
 */
public class ChangeRatingCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String RATING_CHANGE_ERROR = "profile.could_not_change_rating";
    private static final String RATING_CHANGED = "profile.rating_changed";

    public ChangeRatingCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
            int value = Integer.parseInt(data.getRequestParameter(ApplicationConstants.CHANGE_RATING_PARAMETER));
            try {
                AccountService accountService = new AccountService();
                if (accountService.addValueToRating(accountId, value)) {
                    data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, RATING_CHANGED);
                    logger.log(Level.INFO, "account id=" + accountId + " rating changed by " + value);
                } else {
                    data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, RATING_CHANGE_ERROR);
                    logger.log(Level.WARN, "could not change account id=" + accountId + " rating");
                }
            } catch (ServiceException e) {
                throw new CommandException("unable to change account id=" + accountId + " rating", e);
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse parameter", e);
        }
        return commandResult;
    }
}
