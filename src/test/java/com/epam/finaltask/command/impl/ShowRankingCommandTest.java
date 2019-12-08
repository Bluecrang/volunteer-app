package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowRankingCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ShowRankingCommand showRankingCommand;

    private String page = "1";
    private int intPage = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_rankingPageAttributesSet() throws ServiceException, CommandException {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1));
        accounts.add(new Account(2));
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(accountService.findRatingPageAccounts(intPage, 10))
                .thenReturn(accounts);
        when(accountService.countAccounts())
                .thenReturn(2);

        CommandResult actual = showRankingCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.RANKING_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(accountService).findRatingPageAccounts(intPage, 10);
        verify(accountService).countAccounts();
        verify(commandData).putRequestAttribute(ApplicationConstants.RANKING_PAGE_COUNT_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.RANKING_CURRENT_PAGE_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, 5);
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(accountService.findRatingPageAccounts(intPage, 10))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            showRankingCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(accountService).findRatingPageAccounts(intPage, 10);
    }

    @Test
    public void performAction_unparsablePageParameter_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn("vqeqve");

        Assert.assertThrows(CommandException.class, () -> {
            showRankingCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
    }
}
