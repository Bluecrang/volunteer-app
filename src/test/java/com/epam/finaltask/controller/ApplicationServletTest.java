package com.epam.finaltask.controller;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.impl.CommandFactory;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.CommandType;
import com.epam.finaltask.util.ApplicationConstants;
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

public class ApplicationServletTest {

    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;
    @Mock
    HttpSession session;
    @Mock
    CommandFactory commandFactory = mock(CommandFactory.class);
    @InjectMocks
    private ApplicationServlet applicationServlet;

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
    public void doGet_validCommand_commandExecuted() throws ServletException, IOException, CommandException {
        Command command = mock(Command.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        when(httpServletRequest.getMethod())
                .thenReturn("GET");
        when(commandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any()))
                .thenReturn(commandResult);
        
        applicationServlet.doGet(httpServletRequest, httpServletResponse);

        verify(commandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any());
    }

    @Test
    public void doGet_commandException_servletException() throws CommandException {
        Command command = mock(Command.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        when(httpServletRequest.getMethod())
                .thenReturn("GET");
        when(commandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any()))
                .thenThrow(new CommandException());

        Assert.assertThrows(ServletException.class, () -> {
            applicationServlet.doGet(httpServletRequest, httpServletResponse);
        });
        verify(commandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any());
    }

    @Test
    public void doGet_validCommandSessionInvalidationFlagSet_commandExecutedSessionCleared()
            throws ServletException, IOException, CommandException {
        Command command = mock(Command.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        commandResult.raiseSessionInvalidationFlag();
        when(httpServletRequest.getMethod())
                .thenReturn("GET");
        when(commandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any()))
                .thenReturn(commandResult);

        applicationServlet.doGet(httpServletRequest, httpServletResponse);

        verify(commandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any());
        verify(session).invalidate();
    }

    @Test
    public void doPost_validCommand_commandExecuted() throws ServletException, IOException, CommandException {
        Command command = mock(Command.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        when(httpServletRequest.getMethod())
                .thenReturn("POST");
        when(commandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any()))
                .thenReturn(commandResult);

        applicationServlet.doPost(httpServletRequest, httpServletResponse);

        verify(commandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any());
    }

    @Test
    public void doPost_commandException_servletException() throws CommandException {
        Command command = mock(Command.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        when(httpServletRequest.getMethod())
                .thenReturn("POST");
        when(commandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any()))
                .thenThrow(new CommandException());

        Assert.assertThrows(ServletException.class, () -> {
            applicationServlet.doPost(httpServletRequest, httpServletResponse);
        });
        verify(commandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any());
    }

    @Test
    public void doPost_validCommandSessionInvalidationFlagSet_commandExecutedSessionCleared()
            throws ServletException, IOException, CommandException {
        Command command = mock(Command.class);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        commandResult.raiseSessionInvalidationFlag();
        when(httpServletRequest.getMethod())
                .thenReturn("POST");
        when(commandFactory.defineCommand(httpServletRequest))
                .thenReturn(command);
        when(command.execute(any()))
                .thenReturn(commandResult);

        applicationServlet.doPost(httpServletRequest, httpServletResponse);

        verify(commandFactory).defineCommand(httpServletRequest);
        verify(command).execute(any());
        verify(session).invalidate();
    }
}
