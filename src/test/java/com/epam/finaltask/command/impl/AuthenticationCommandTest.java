package com.epam.finaltask.command.impl;

import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.AuthenticationException;
import com.epam.finaltask.service.AuthenticationService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class AuthenticationCommandTest {

    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private AuthenticationCommand authenticationCommand;

    private String email = "email@mail.ru";
    private String password = "password";

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
    }

    @Test
    public void performAction_authenticationDataValid_moveToMainPage() throws ServiceException {
        Account account = new Account();
        when(authenticationService.authenticate(email, password))
                .thenReturn(account);

        CommandResult actual = authenticationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_MAIN_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(authenticationService).authenticate(email, password);
        verify(commandData).putSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE, account);
    }

    @Test
    public void performAction_accountBlocked_moveToLoginPage() throws ServiceException {
        Account account = new Account();
        account.setBlocked(true);
        when(authenticationService.authenticate(email, password))
                .thenReturn(account);

        CommandResult actual = authenticationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_LOGIN_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(authenticationService).authenticate(email, password);
        verify(commandData, never()).putSessionAttribute(eq(ApplicationConstants.ACCOUNT_ATTRIBUTE), any());
    }

    @Test
    public void performAction_authenticationException_moveToLoginPage() throws ServiceException {
        when(authenticationService.authenticate(email, password))
                .thenThrow(new AuthenticationException());

        CommandResult actual = authenticationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_LOGIN_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(authenticationService).authenticate(email, password);
        verify(commandData, never()).putSessionAttribute(eq(ApplicationConstants.ACCOUNT_ATTRIBUTE), any());
    }

    @Test
    public void performAction_serviceException_moveToLoginPage() throws ServiceException {
        when(authenticationService.authenticate(email, password))
                .thenThrow(new ServiceException());

        CommandResult actual = authenticationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_LOGIN_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(authenticationService).authenticate(email, password);
        verify(commandData, never()).putSessionAttribute(eq(ApplicationConstants.ACCOUNT_ATTRIBUTE), any());
    }
}
