package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class ChangeAccountTypeCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ChangeAccountTypeCommand changeAccountTypeCommand;

    private String accountId = "1";
    private long longAccountId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_accountTypeChanged_successNotificationSet() throws ServiceException, CommandException {
        String accountType = "USER";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER))
                .thenReturn(accountType);
        when(accountService.changeAccountType(longAccountId, AccountType.USER))
            .thenReturn(true);

        CommandResult actual = changeAccountTypeCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + accountId);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER);
        verify(accountService).changeAccountType(longAccountId, AccountType.USER);
        verify(commandData).putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE,
                "profile.account_promoted");
    }

    @Test
    public void performAction_accountTypeNotChanged_failureNotificationSet() throws ServiceException, CommandException {
        String accountType = "USER";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER))
                .thenReturn(accountType);
        when(accountService.changeAccountType(longAccountId, AccountType.USER))
                .thenReturn(false);

        CommandResult actual = changeAccountTypeCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + accountId);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER);
        verify(accountService).changeAccountType(longAccountId, AccountType.USER);
        verify(commandData).putSessionAttribute(ApplicationConstants.PROFILE_ACTION_NOTIFICATION_ATTRIBUTE,
                "profile.promotion_error");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        String accountType = "USER";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER))
                .thenReturn(accountType);
        when(accountService.changeAccountType(longAccountId, AccountType.USER))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            changeAccountTypeCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER);
        verify(accountService).changeAccountType(longAccountId, AccountType.USER);
    }

    @Test
    public void performAction_unparsableAccountId_commandException() {
        String accountType = "USER";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn("efqfqeq");
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER))
                .thenReturn(accountType);

        Assert.assertThrows(CommandException.class, () -> {
            changeAccountTypeCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
    }

    @Test
    public void performAction_unparsableAccountType_commandException() {
        String accountType = "feiqjfq";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER))
                .thenReturn(accountType);

        Assert.assertThrows(CommandException.class, () -> {
            changeAccountTypeCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_TYPE_PARAMETER);
    }
}
