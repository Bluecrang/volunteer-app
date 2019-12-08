package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
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

import javax.servlet.http.Part;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class UploadAvatarCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @Mock
    private Part part;
    @InjectMocks
    private UploadAvatarCommand uploadAvatarCommand;
    private List<Part> parts = new LinkedList<>();

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        parts.clear();
        parts.add(part);
    }

    @Test
    public void performAction_noErrors_errorMessageNotSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(accountService.updateAvatar(account, part))
                .thenReturn(true);

        CommandResult actual = uploadAvatarCommand.performAction(commandData, parts);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + account.getAccountId());
        verify(commandData).getSessionAccount();
        verify(accountService).updateAvatar(account, part);
        verify(commandData, never()).putRequestAttribute("action_message", "profile.action_message.unable_to_upload_avatar");
    }

    @Test
    public void performAction_uploadingError_errorMessageSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(accountService.updateAvatar(account, part))
                .thenReturn(false);

        CommandResult actual = uploadAvatarCommand.performAction(commandData, parts);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_PROFILE + account.getAccountId());
        verify(commandData).getSessionAccount();
        verify(accountService).updateAvatar(account, part);
        verify(commandData).putRequestAttribute("action_message", "profile.action_message.unable_to_upload_avatar");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(accountService.updateAvatar(account, part))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            uploadAvatarCommand.performAction(commandData, parts);
        });
        verify(commandData).getSessionAccount();
        verify(accountService).updateAvatar(account, part);
    }

    @Test
    public void performAction_sessionAccountNull_commandException() {
        when(commandData.getSessionAccount())
                .thenReturn(null);

        Assert.assertThrows(CommandException.class, () -> {
            uploadAvatarCommand.performAction(commandData, parts);
        });
        verify(commandData).getSessionAccount();
    }
}
