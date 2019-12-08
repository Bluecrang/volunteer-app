package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.service.RegistrationService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationCommandTest {

    @Mock
    private RegistrationService registrationService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private RegistrationCommand RegistrationCommand;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_success() throws ServiceException, CommandException {
        String username = "username";
        String password = "password";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);
        when(registrationService.registerAccount(username, password, email))
                .thenReturn(RegistrationService.RegistrationResult.SUCCESS);

        CommandResult actual = RegistrationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_LOGIN_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(registrationService).registerAccount(username, password, email);
    }

    @Test
    public void performAction_emailTaken_emailExists() throws ServiceException, CommandException {
        String username = "username";
        String password = "password";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);
        when(registrationService.registerAccount(username, password, email))
                .thenReturn(RegistrationService.RegistrationResult.EMAIL_EXISTS);

        CommandResult actual = RegistrationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_REGISTRATION_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(registrationService).registerAccount(username, password, email);
    }

    @Test
    public void performAction_usernameTaken_usernameExists() throws ServiceException, CommandException {
        String username = "username";
        String password = "password";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);
        when(registrationService.registerAccount(username, password, email))
                .thenReturn(RegistrationService.RegistrationResult.USERNAME_EXISTS);

        CommandResult actual = RegistrationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_REGISTRATION_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(registrationService).registerAccount(username, password, email);
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        String username = "username";
        String password = "password";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);
        when(registrationService.registerAccount(username, password, email))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            RegistrationCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(registrationService).registerAccount(username, password, email);
    }

    @Test
    public void performAction_databaseProblem_commandException() throws ServiceException {
        String username = "username";
        String password = "password";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);
        when(registrationService.registerAccount(username, password, email))
                .thenReturn(RegistrationService.RegistrationResult.CANNOT_CREATE_ACCOUNT_IN_DATABASE);

        Assert.assertThrows(CommandException.class, () -> {
            RegistrationCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(registrationService).registerAccount(username, password, email);
    }

    @Test
    public void performAction_emailInvalid_errorMessageSet() throws CommandException {
        String username = "username";
        String password = "password";
        String email = "email@ma";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);

        CommandResult actual = RegistrationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_REGISTRATION_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, "registration.illegal_email");
    }

    @Test
    public void performAction_usernameInvalid_errorMessageSet() throws CommandException {
        String username = "u";
        String password = "password";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);

        CommandResult actual = RegistrationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_REGISTRATION_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, "registration.illegal_username");
    }

    @Test
    public void performAction_passwordInvalid_errorMessageSet() throws CommandException {
        String username = "username";
        String password = "pas";
        String email = "email@mail.ru";
        when(commandData.getRequestParameter(ApplicationConstants.USERNAME_PARAMETER))
                .thenReturn(username);
        when(commandData.getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER))
                .thenReturn(password);
        when(commandData.getRequestParameter(ApplicationConstants.EMAIL_PARAMETER))
                .thenReturn(email);

        CommandResult actual = RegistrationCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_REGISTRATION_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.USERNAME_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PASSWORD_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.EMAIL_PARAMETER);
        verify(commandData).putRequestAttribute(ApplicationConstants.REGISTRATION_MESSAGE_ATTRIBUTE, "registration.illegal_password");
    }
}
