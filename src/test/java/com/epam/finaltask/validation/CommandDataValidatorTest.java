package com.epam.finaltask.validation;

import com.epam.finaltask.command.impl.CommandConstraints;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.command.impl.HttpMethodType;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandDataValidatorTest {

    private CommandDataValidator validator = new CommandDataValidator();
    @Mock
    private CommandData commandData;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateTestAllowedData() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.ADMIN)
                .buildHttpMethods(HttpMethodType.POST)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.POST);
        Account account = new Account(1);
        account.setAccountType(AccountType.ADMIN);
        when(commandData.getSessionAccount()).thenReturn(account);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getSessionAccount();
        Assert.assertTrue(result);
    }

    @Test
    public void validateTestIllegalHttpMethod() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.ADMIN)
                .buildHttpMethods(HttpMethodType.POST)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.GET);
        Account account = new Account(1);
        account.setAccountType(AccountType.ADMIN);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getMethod();
        Assert.assertFalse(result);
    }

    @Test
    public void validateTestIllegalAccountType() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.USER)
                .buildHttpMethods(HttpMethodType.PUT)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.PUT);
        Account account = new Account(1);
        account.setAccountType(AccountType.ADMIN);
        when(commandData.getSessionAccount()).thenReturn(account);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getSessionAccount();
        Assert.assertFalse(result);
    }

    @Test
    public void validateTestSessionAccountNullConstraintAccountTypeUser() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.USER)
                .buildHttpMethods(HttpMethodType.PUT)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.PUT);
        Account account = null;
        when(commandData.getSessionAccount()).thenReturn(account);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getMethod();
        verify(commandData).getSessionAccount();
        Assert.assertFalse(result);
    }

    @Test
    public void validateTestSessionAccountNullConstraintAccountTypeGuest() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.GUEST)
                .buildHttpMethods(HttpMethodType.GET)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.GET);
        Account account = null;
        when(commandData.getSessionAccount()).thenReturn(account);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getMethod();
        verify(commandData).getSessionAccount();
        Assert.assertTrue(result);
    }

    @Test
    public void validateTestSessionAccountsAccountTypeNullConstraintAccountTypeAdmin() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.ADMIN)
                .buildHttpMethods(HttpMethodType.GET)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.GET);
        Account account = new Account(1);
        when(commandData.getSessionAccount()).thenReturn(account);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getMethod();
        verify(commandData).getSessionAccount();
        Assert.assertFalse(result);
    }

    @Test
    public void validateTestSessionAccountsAccountTypeNullConstraintAccountTypeGuest() {
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.GUEST)
                .buildHttpMethods(HttpMethodType.GET)
                .build();

        when(commandData.getMethod()).thenReturn(HttpMethodType.GET);
        Account account = new Account(1);
        when(commandData.getSessionAccount()).thenReturn(account);

        boolean result = validator.validate(commandData, commandConstraints);

        verify(commandData).getMethod();
        verify(commandData).getSessionAccount();
        Assert.assertTrue(result);
    }
}
