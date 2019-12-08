package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandFactoryTest {

    @Mock
    HttpServletRequest request;

    private CommandFactory commandFactory = CommandFactory.getInstance();

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void defineCommand_requestWithValidCommand_commandReturned() {
        String command = "REGISTRATION";
        when(request.getParameter(ApplicationConstants.COMMAND_PARAMETER))
                .thenReturn(command);

        Command actual = commandFactory.defineCommand(request);

        Assert.assertEquals(actual, CommandType.REGISTRATION.getCommand());
        verify(request).getParameter(ApplicationConstants.COMMAND_PARAMETER);
    }

    @Test
    public void defineCommand_requestWithUnknownCommand_moveToIndexPageCommandReturned() {
        String command = "fqegq";
        when(request.getParameter(ApplicationConstants.COMMAND_PARAMETER))
                .thenReturn(command);

        Command actual = commandFactory.defineCommand(request);

        Assert.assertEquals(actual, CommandType.MOVE_TO_INDEX_PAGE.getCommand());
        verify(request).getParameter(ApplicationConstants.COMMAND_PARAMETER);
    }
}
