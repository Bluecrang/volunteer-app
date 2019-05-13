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

import java.util.List;

public class ShowRankingCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final int NUMBER_OF_ACCOUNTS_PER_PAGE = 5;
    private static final String NUMBER_OF_ACCOUNTS_PER_PAGE_ATTRIBUTE = "accounts_per_page";
    private static final String ACCOUNT_LIST_ATTRIBUTE = "account_list";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
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
            data.putRequestAttribute(NUMBER_OF_ACCOUNTS_PER_PAGE_ATTRIBUTE, NUMBER_OF_ACCOUNTS_PER_PAGE);
            commandResult.setPage(PageConstants.RANKING_PAGE);
        } catch (ServiceException e) {
            throw new CommandException("Unable to show rating page: ServiceException has occurred", e);
        } catch (NumberFormatException e) {
            throw new CommandException("Unable to parse parameter", e);
        }
        return commandResult;
    }
}
