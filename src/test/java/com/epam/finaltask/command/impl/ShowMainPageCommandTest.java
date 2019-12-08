package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.PageConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShowMainPageCommandTest {

    private ShowMainPageCommand showMainPageCommand = new ShowMainPageCommand(null);

    @Test
    public void performAction_callPerformed_resultPageMainPage() {
        CommandResult actual = showMainPageCommand.performAction(new CommandData());

        Assert.assertEquals(actual.getPage(), PageConstants.MAIN_PAGE);
    }
}
