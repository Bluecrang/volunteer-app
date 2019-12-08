package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.PageConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShowRegistrationPageCommandTest {

    private ShowRegistrationPageCommand registrationPageCommand = new ShowRegistrationPageCommand(null);

    @Test
    public void performAction_callPerformed_resultPageMainPage() {
        CommandResult actual = registrationPageCommand.performAction(new CommandData());

        Assert.assertEquals(actual.getPage(), PageConstants.REGISTRATION_PAGE);
    }
}
