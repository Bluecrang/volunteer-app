package com.epam.finaltask.controller;

import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractServlet extends HttpServlet {

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
            default: {
                throw new EnumConstantNotPresentException(commandResult.getTransitionType().getClass(),
                        "TransitionType constant is not present");
            }
        }
    }
}
