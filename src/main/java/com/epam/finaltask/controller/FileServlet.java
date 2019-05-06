package com.epam.finaltask.controller;

import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.command.UploadCommand;
import com.epam.finaltask.command.impl.UploadCommandFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileServlet extends AbstractServlet {

    private static final Logger logger = LogManager.getLogger();
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        UploadCommandFactory uploadCommandFactory = new UploadCommandFactory();
        try {
            UploadCommand uploadCommand = uploadCommandFactory.defineCommand(request);
            logger.log(Level.INFO, "defined command=" + uploadCommand);
            CommandData commandData = new CommandData(request);
            CommandResult commandResult = uploadCommand.execute(commandData, request.getParts());
            commandData.updateSessionAttributes(request.getSession());
            logger.log(Level.DEBUG, "TransitionType: " + commandResult.getTransitionType());
            performTransition(request, response, commandResult, commandData);
        } catch (CommandException e) {
            throw new ServletException(e);
        }
    }
}
