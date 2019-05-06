package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShowProfileCommand implements Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String PROFILE_ATTRIBUTE = "profile";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        commandResult.setPage(PageConstants.INDEX_PAGE);
        try {
            long accountId = Long.parseLong(data.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER));
            try {
                AccountService accountService = new AccountService();
                Account account = accountService.findAccountById(accountId);
                if (account == null) {
                    throw new CommandException("Could not find account by chosen id, id=" + accountId); //todo
                }
                logger.log(Level.INFO, "account id=" + accountId + " found");
                data.putRequestAttribute(PROFILE_ATTRIBUTE, account);
                commandResult.setPage(PageConstants.PROFILE_PAGE);
            } catch (ServiceException e) {
                throw new CommandException("could not find account with id=" + accountId, e);
            }
        } catch (NumberFormatException e) { //TODO NumberFormatException catch
            throw new CommandException("could not parse account id to long value", e);
        }
        return commandResult;
    }
}
