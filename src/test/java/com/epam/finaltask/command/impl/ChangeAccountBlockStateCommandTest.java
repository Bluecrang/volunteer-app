package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
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

public class ChangeAccountBlockStateCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ChangeAccountBlockStateCommand changeAccountBlockStateCommand;

    private String accountId = "1";
    private long longAccountId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_blockStateChanged_noException() throws ServiceException, CommandException {
        String block = "true";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter("block"))
                .thenReturn(block);
        when(accountService.changeAccountBlockState(Long.parseLong(accountId), Boolean.valueOf(block)))
                .thenReturn(true);

        CommandResult actual = changeAccountBlockStateCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + accountId);
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter("block");
        verify(accountService).changeAccountBlockState(longAccountId, true);
    }

    @Test
    public void performAction_blockStateNotChanged_commandException() throws ServiceException {
        String block = "true";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter("block"))
                .thenReturn(block);
        when(accountService.changeAccountBlockState(Long.parseLong(accountId), Boolean.valueOf(block)))
                .thenReturn(false);

        Assert.assertThrows(CommandException.class, () -> {
            changeAccountBlockStateCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter("block");
        verify(accountService).changeAccountBlockState(longAccountId, true);
    }

    @Test
    public void performAction_serviceExceptionThrown_commandException() throws ServiceException {
        String block = "true";
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn(accountId);
        when(commandData.getRequestParameter("block"))
                .thenReturn(block);
        when(accountService.changeAccountBlockState(Long.parseLong(accountId), Boolean.valueOf(block)))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            changeAccountBlockStateCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData).getRequestParameter("block");
        verify(accountService).changeAccountBlockState(longAccountId, true);
    }

    @Test
    public void performAction_accountIdUnparsable_commandException() throws ServiceException {
        when(commandData.getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER))
                .thenReturn("aef");

        Assert.assertThrows(CommandException.class, () -> {
            changeAccountBlockStateCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.ACCOUNT_ID_PARAMETER);
        verify(commandData, never()).getRequestParameter("block");
    }
}
