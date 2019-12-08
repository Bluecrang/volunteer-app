package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.PageConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShowLoginPageCommandTest {

    private ShowLoginPageCommand showLoginPageCommand = new ShowLoginPageCommand(null);

    @Test
    public void performAction_callPerformed_resultPageLoginPage() {
        CommandResult actual = showLoginPageCommand.performAction(new CommandData());

        Assert.assertEquals(actual.getPage(), PageConstants.LOGIN_PAGE);
    }
}
