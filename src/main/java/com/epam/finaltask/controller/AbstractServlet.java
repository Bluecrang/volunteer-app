package com.epam.finaltask.controller;

import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.command.impl.CommandResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Abstract servlet which provides general methods.
 */
public abstract class AbstractServlet extends HttpServlet {

    /**
     * Performs transition to the page from commandResult. Type of transition is defined by commandResult's transitionType.
     * @param request Http servlet request
     * @param response Http servlet response
     * @param commandResult Result of the performed command which contains page and transition type.
     * @param commandData Command data which provides fresh request attributes to the request
     * @throws IOException If a problem occurs while forwarding, redirecting or error sending
     * @throws ServletException If there is a problem while forwarding
     */
    void performTransition(HttpServletRequest request, HttpServletResponse response,
                           CommandResult commandResult, CommandData commandData) throws IOException, ServletException {
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
            case ERROR: {
                response.sendError(commandResult.getCode());
                break;
            }
            default: {
                throw new EnumConstantNotPresentException(commandResult.getTransitionType().getClass(),
                        "TransitionType constant is not present");
            }
        }
    }
}
