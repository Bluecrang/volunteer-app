package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.AccountService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.PageConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ShowAdministratorsCommandTest {

    @Mock
    private AccountService accountService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ShowAdministratorsCommand showAdministratorsCommand;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_TwoAdministratorsExist_twoAdministratorsFound() throws ServiceException, CommandException {
        List<Account> administrators = new ArrayList<Account>() {
            {
                Account admin1 = new Account();
                admin1.setAccountType(AccountType.ADMIN);
                add(admin1);

                Account admin2 = new Account();
                admin2.setAccountType(AccountType.ADMIN);
                add(admin2);
            }
        };
        when(accountService.findAdministrators())
                .thenReturn(administrators);

        CommandResult actual = showAdministratorsCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.ADMINISTRATORS_PAGE);
        verify(accountService).findAdministrators();
        verify(commandData).putRequestAttribute("account_list", administrators);
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        when(accountService.findAdministrators())
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            showAdministratorsCommand.performAction(commandData);
        });
        verify(accountService).findAdministrators();
        verify(commandData, never()).putRequestAttribute(eq("account_list"), any());
    }
}
