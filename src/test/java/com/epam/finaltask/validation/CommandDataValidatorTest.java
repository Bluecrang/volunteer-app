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

    @Test
    public void validate_dataNullConstraintsNull_false() {
        CommandData commandData = null;
        CommandConstraints commandConstraints = null;

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_dataNotNullConstraintsNull_false() {
        CommandData commandData = new CommandData(null);
        CommandConstraints commandConstraints = null;

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_illegalHttpMethod_false() {
        CommandData commandData = new CommandData(null);
        CommandConstraints commandConstraints = CommandConstraints.builder().build();

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_dataAccountNull_false() {
        CommandData commandData = new CommandData(null);
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildHttpMethods(HttpMethodType.UNDEFINED)
                .build();

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_sessionAccountAccountTypeGuestConstraintsAccountTypesNone_false() {
        CommandData commandData = new CommandData(HttpMethodType.UNDEFINED, new Account());
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildHttpMethods(HttpMethodType.UNDEFINED)
                .build();

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_sessionAccountAccountTypeGuestConstraintsAccountTypeVolunteer_false() {
        CommandData commandData = new CommandData(HttpMethodType.UNDEFINED, new Account());
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildHttpMethods(HttpMethodType.UNDEFINED)
                .buildAccountTypes(AccountType.VOLUNTEER)
                .build();

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertFalse(actual);
    }

    @Test
    public void validate_sessionAccountAccountTypeGuestConstraintAccountTypeGuest_true() {
        CommandData commandData = new CommandData(HttpMethodType.UNDEFINED, new Account());
        CommandConstraints commandConstraints = CommandConstraints.builder()
                .buildAccountTypes(AccountType.GUEST)
                .buildHttpMethods(HttpMethodType.UNDEFINED)
                .build();

        boolean actual = validator.validate(commandData, commandConstraints);

        Assert.assertTrue(actual);
    }
}