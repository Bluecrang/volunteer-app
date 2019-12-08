package com.epam.finaltask.controller;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.command.impl.CommandFactory;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.connectionpool.ConnectionPool;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Application's main servlet. Initializes connection pool in init().
 * Accepts and processes POST and GET requests.
 */
@WebServlet(urlPatterns = {"/controller"})
public class ApplicationServlet extends AbstractServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final int POOL_MAINTENANCE_PERIOD_MILLIS = 1000 * 60 * 60;
    private static final String POOL_PROPERTIES_FILENAME = "/WEB-INF/pool.properties";

    private CommandFactory commandFactory;

    public ApplicationServlet() {
        this.commandFactory = CommandFactory.getInstance();
    }

    public ApplicationServlet(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Initializes connection pool.
     */
    @Override
    public void init() {
        String configFilename = getServletContext().getRealPath("/") + POOL_PROPERTIES_FILENAME;
        ConnectionPool.INSTANCE.init(configFilename, POOL_MAINTENANCE_PERIOD_MILLIS);
    }

    /**
     * Delegates request procession to {@link ApplicationServlet#processRequest(HttpServletRequest, HttpServletResponse)}.
     * @param request Received HttpServletRequest
     * @param response Received HttpServletResponse
     * @throws IOException If it is thrown by {@link ApplicationServlet#processRequest(HttpServletRequest, HttpServletResponse)}
     * @throws ServletException If it is thrown by {@link ApplicationServlet#processRequest(HttpServletRequest, HttpServletResponse)}
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    /**
     * Delegates request procession to {@link ApplicationServlet#processRequest(HttpServletRequest, HttpServletResponse)}.
     * @param request Received HttpServletRequest
     * @param response Received HttpServletResponse
     * @throws IOException If it is thrown by {@link ApplicationServlet#processRequest(HttpServletRequest, HttpServletResponse)}
     * @throws ServletException If it is thrown by {@link ApplicationServlet#processRequest(HttpServletRequest, HttpServletResponse)}
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    /**
     * Closes connection pool.
     */
    @Override
    public void destroy() {
        ConnectionPool.INSTANCE.closePool();
    }

    /**
     * Processes GET or POST request.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException If {@link AbstractServlet#performTransition(HttpServletRequest, HttpServletResponse, CommandResult, CommandData)} throws IOException
     * @throws ServletException If performTransition throws ServletException or CommandException is thrown while executing command
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Command command = commandFactory.defineCommand(request);
        logger.log(Level.INFO, "defined command=" + command);
        CommandData commandData = new CommandData(request);
        CommandResult commandResult;
        try {
            commandResult = command.execute(commandData);
        } catch (CommandException e) {
            throw new ServletException("could not execute command " + command, e);
        }
        commandData.updateSessionAttributes(request.getSession());
        logger.log(Level.DEBUG, "TransitionType: " + commandResult.getTransitionType());
        if (commandResult.isSessionInvalidationFlag()) {
            request.getSession().invalidate();
        }
        performTransition(request, response, commandResult, commandData);
    }
}
