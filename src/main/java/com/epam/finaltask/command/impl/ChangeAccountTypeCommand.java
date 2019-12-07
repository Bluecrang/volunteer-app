package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that is used to promote user to administrator.
 */
public class ChangeAccountTypeCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String PROMOTION_ERROR = "profile.promotion_error";
    private static final String ACCOUNT_PROMOTED = "profile.account_promoted";

    private AccountService accountService;

    public ChangeAccountTypeCommand(CommandConstraints constraints) {
        super(constraints);
        this.accountService = new AccountService();
    }

    public ChangeAccountTypeCommand(CommandConstraints constraints, AccountService accountService) {
        super(constraints);
        this.accountService = accountService;

    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_PROFILE + accountId);
            try {
                String accountTypeString = data.getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER);
                AccountType accountType = AccountType.valueOf(accountTypeString.toUpperCase());
                if (accountService.changeAccountType(accountId, accountType)) {
                    data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, ACCOUNT_PROMOTED);
                    logger.log(Level.INFO, "account id=" + accountId + " promoted to admin");
                } else {
                    data.putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE, PROMOTION_ERROR);
                    logger.log(Level.WARN, "could not promote account id="+ accountId + " to admin");
                }
            } catch (ServiceException e) {
                throw new CommandException("unable to promote account to admin", e);
            } catch (IllegalArgumentException e) {
                throw new CommandException("could not define account type using chosen account type string", e);
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
