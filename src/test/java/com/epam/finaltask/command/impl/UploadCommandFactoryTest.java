package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.UploadCommand;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploadCommandFactoryTest {

    @Mock
    HttpServletRequest request;

    private UploadCommandFactory uploadCommandFactory = new UploadCommandFactory();

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void defineCommand_requestWithValidCommand_commandReturned() throws CommandException {
        String command = "UPLOAD_AVATAR";
        when(request.getParameter(ApplicationConstants.COMMAND_PARAMETER))
                .thenReturn(command);

        UploadCommand actual = uploadCommandFactory.defineCommand(request);

        Assert.assertEquals(actual, UploadCommandType.UPLOAD_AVATAR.getUploadCommand());
        verify(request).getParameter(ApplicationConstants.COMMAND_PARAMETER);
    }

    @Test
    public void defineCommand_requestWithUnknownCommand_commandException() {
        String command = "fqegq";
        when(request.getParameter(ApplicationConstants.COMMAND_PARAMETER))
                .thenReturn(command);

        Assert.assertThrows(CommandException.class, () -> {
            uploadCommandFactory.defineCommand(request);
        });
        verify(request).getParameter(ApplicationConstants.COMMAND_PARAMETER);
    }
}
