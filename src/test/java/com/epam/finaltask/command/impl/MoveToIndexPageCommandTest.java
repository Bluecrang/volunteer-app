package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.PageConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MoveToIndexPageCommandTest {

    private MoveToIndexPageCommand moveToIndexPageCommand = new MoveToIndexPageCommand(null);

    @Test
    public void performAction_localeProvided_sessionLocaleSet() {
        CommandResult actual = moveToIndexPageCommand.performAction(new CommandData());

        Assert.assertEquals(actual.getPage(), PageConstants.INDEX_PAGE);
    }
}
