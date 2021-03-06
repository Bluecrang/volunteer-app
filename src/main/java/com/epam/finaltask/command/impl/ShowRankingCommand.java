package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command that is used to initialize data for the ranking page.
 */
public class ShowRankingCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final int NUMBER_OF_ACCOUNTS_PER_PAGE = 10;
    private static final String ACCOUNT_LIST_ATTRIBUTE = "account_list";
    private static final Integer PAGE_STEP = 5;

    public ShowRankingCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            int currentPage = Integer.parseInt(data.getRequestParameter(ApplicationConstants.PAGE_PARAMETER));
            AccountService accountService = new AccountService();
            List<Account> accountList = accountService.findRatingPageAccounts(currentPage, NUMBER_OF_ACCOUNTS_PER_PAGE);
            logger.log(Level.DEBUG, "account list: " + accountList);
            data.putRequestAttribute(ACCOUNT_LIST_ATTRIBUTE, accountList);
            int accountCount = accountService.countAccounts();
            int numberOfPages = Math.toIntExact(Math.round(Math.ceil((double)accountCount / NUMBER_OF_ACCOUNTS_PER_PAGE)));
            data.putRequestAttribute(ApplicationConstants.RANKING_PAGE_COUNT_ATTRIBUTE, numberOfPages);
            data.putRequestAttribute(ApplicationConstants.RANKING_CURRENT_PAGE_ATTRIBUTE, currentPage);
            data.putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, PAGE_STEP);
            commandResult.setPage(PageConstants.RANKING_PAGE);
        } catch (ServiceException e) {
            throw new CommandException("Unable to show rating page: ServiceException has occurred", e);
        } catch (NumberFormatException e) {
            throw new CommandException("Unable to parse parameter", e);
        }
        return commandResult;
    }
}
