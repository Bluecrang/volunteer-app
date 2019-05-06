package com.epam.finaltask.controller;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.impl.CommandException;
import com.epam.finaltask.command.impl.CommandFactory;
import com.epam.finaltask.command.impl.CommandResult;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.connectionpool.ConnectionPool;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/servlet", "/topics", "/topic", "/profile", "/ranking"})
public class ApplicationServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();
    private static final int POOL_MAINTENANCE_PERIOD_MILLIS = 1000 * 60 * 60;
    private static final String POOL_PROPERTIES_FILENAME = "/WEB-INF/pool.properties";

    @Override
    public void init() {
        String configFilename = getServletContext().getRealPath("/") + POOL_PROPERTIES_FILENAME;
        ConnectionPool.instance.init(configFilename, POOL_MAINTENANCE_PERIOD_MILLIS);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void destroy() {
        ConnectionPool.instance.closePool();
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        CommandFactory commandFactory = new CommandFactory();
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
        switch (commandResult.getTransitionType()) {
            case FORWARD: {
                commandData.updateRequestAttributes(request);
                request.getRequestDispatcher(commandResult.getPage()).forward(request, response);
                break;
            }
            case REDIRECT: {
                response.sendRedirect(getServletContext().getContextPath() + commandResult.getPage());
                break;
            }
            default: {
                throw new EnumConstantNotPresentException(commandResult.getTransitionType().getClass(),
                        "TransitionType constant is not present");
            }
        }
    }
}
