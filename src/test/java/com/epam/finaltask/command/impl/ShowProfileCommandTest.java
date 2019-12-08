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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowProfileCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ShowProfileCommand showProfileCommand;

    private String accountId = "1";
    private long longAccountId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_accountAttributeSet() throws ServiceException, CommandException {
        Account account = new Account(longAccountId);
        account.setAccountType(AccountType.VOLUNTEER);
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(accountService.findAccountById(longAccountId))
                .thenReturn(account);

        CommandResult actual = showProfileCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.PROFILE_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(accountService).findAccountById(longAccountId);
        verify(commandData).putRequestAttribute("profile", account);
    }

    @Test
    public void performAction_accountDoesNotExist_commandException() throws ServiceException {
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(accountService.findAccountById(longAccountId))
                .thenReturn(null);

        Assert.assertThrows(CommandException.class, () -> {
            showProfileCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(accountService).findAccountById(longAccountId);
    }

    @Test
    public void performAction_unparsableAccountId_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn("feqfq");

        Assert.assertThrows(CommandException.class, () -> {
            showProfileCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(accountService.findAccountById(longAccountId))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            showProfileCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(accountService).findAccountById(longAccountId);
    }
}
