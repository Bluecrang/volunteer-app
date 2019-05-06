package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
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
        int currentPage = Integer.parseInt(data.getRequestParameter(ApplicationConstants.PAGE_PARAMETER)); //todo validate
        boolean loadAccounts = Boolean.valueOf(data.getRequestParameter(ApplicationConstants.TOPIC_LOAD_ACCOUNTS_PARAMETER));
        try {
            if (loadAccounts) {
                AccountService accountService = new AccountService();
                List<Account> accountList = accountService.findAllAccounts();
                accountList.sort(Comparator.comparing(Account::getRating, Comparator.reverseOrder()));
                int numberOfPages = Math.toIntExact(Math.round(Math.ceil((double)accountList.size() / NUMBER_OF_ACCOUNTS_PER_PAGE))); //todo handle ArithmeticalException
                data.putSessionAttribute(ACCOUNT_LIST_ATTRIBUTE, accountList);
                data.putSessionAttribute(ApplicationConstants.RANKING_PAGE_COUNT_ATTRIBUTE, numberOfPages);
            }
            data.putRequestAttribute(ApplicationConstants.RANKING_CURRENT_PAGE_ATTRIBUTE, currentPage);
            data.putRequestAttribute(NUMBER_OF_ACCOUNTS_PER_PAGE_ATTRIBUTE, NUMBER_OF_ACCOUNTS_PER_PAGE);
            commandResult.setPage(PageConstants.RANKING_PAGE);
        } catch (ServiceException e) {
            throw new CommandException("Unable to show rating page: ServiceException has occurred", e);
        }
        return commandResult;
    }
}
