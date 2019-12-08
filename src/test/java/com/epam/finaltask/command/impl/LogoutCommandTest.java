package com.epam.finaltask.command.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LogoutCommandTest {

    private LogoutCommand logoutCommand = new LogoutCommand(null);

    @Test
    public void performAction_localeProvided_sessionLocaleSet() {
        CommandResult actual = logoutCommand.performAction(new CommandData());

        Assert.assertTrue(actual.isSessionInvalidationFlag());
    }
}
