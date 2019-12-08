package com.epam.finaltask.controller;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.UploadCommand;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.UploadCommandFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class FileServletTest {

    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    HttpSession session;
    @Mock
    UploadCommandFactory uploadCommandFactory = mock(UploadCommandFactory.class);
    @InjectMocks
    private FileServlet fileServlet;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        when(httpServletRequest.getRequestDispatcher(any()))
                .thenReturn(requestDispatcher);
        when(httpServletRequest.getParameterNames())
                .thenReturn(new Enumeration<String>() {
                    @Override
                    public boolean hasMoreElements() {
                        return false;
                    }

                    @Override
                    public String nextElement() {
                        return null;
                    }
                });
        when(httpServletRequest.getAttributeNames())
                .thenReturn(new Enumeration<String>() {
                    @Override
                    public boolean hasMoreElements() {
                        return false;
                    }

                    @Override
                    public String nextElement() {
                        return null;
                    }
                });
        when(httpServletRequest.getSession())
                .thenReturn(session);
        when(session.getAttributeNames())
                .thenReturn(new Enumeration<String>() {
                    @Override
                    public boolean hasMoreElements() {
                        return false;
                    }

                    @Override
                    public String nextElement() {
                        return null;
                    }
                });
    }

    @Test
    public void doPost_validCommand_commandExecuted() throws ServletException, IOException, CommandException {
        UploadCommand command = mock(UploadCommand.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        when(httpServletRequest.getMethod())
                .thenReturn("POST");
        when(uploadCommandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any(), any()))
                .thenReturn(commandResult);

        fileServlet.doPost(httpServletRequest, httpServletResponse);

        verify(uploadCommandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any(), any());
    }

    @Test
    public void doPost_commandException_servletException() throws CommandException {
        UploadCommand command = mock(UploadCommand.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        when(httpServletRequest.getMethod())
                .thenReturn("POST");
        when(uploadCommandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any(), any()))
                .thenThrow(new CommandException());

        Assert.assertThrows(ServletException.class, () -> {
            fileServlet.doPost(httpServletRequest, httpServletResponse);
        });
        verify(uploadCommandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any(), any());
    }
}
