package com.epam.finaltask.command.impl;

import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeLocaleCommandTest {

    @Mock
    private CommandData commandData;
    private ChangeLocaleCommand changeLocaleCommand = new ChangeLocaleCommand(null);

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_localeProvided_sessionLocaleSet() {
        String locale = "ru";
        when(commandData.getRequestParameter(ApplicationConstants.LOCALE_PARAMETER))
                .thenReturn(locale);

        CommandResult actual = changeLocaleCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.MOVE_TO_INDEX_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.LOCALE_PARAMETER);
        verify(commandData).putSessionAttribute(ApplicationConstants.LOCALE_ATTRIBUTE, locale);
    }
}
