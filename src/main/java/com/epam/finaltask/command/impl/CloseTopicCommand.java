package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloseTopicCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public CommandResult execute(RequestData data) throws CommandException {
        Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult commandResult = new CommandResult();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_TOPIC + topicId);
            if (sessionAccountObject instanceof Account) {
                Account sessionAccount = (Account) sessionAccountObject;
                try {
                    if (sessionAccount.getAccessLevel() == AccessLevel.ADMIN) {
                        TopicService topicService = new TopicService();
                        if (topicService.closeTopic(sessionAccount, topicId)) {
                            logger.log(Level.INFO, "topic id=" + topicId + " closed");
                        } else {
                            logger.log(Level.WARN, "could not close topic id=" + topicId);
                        }
                    }
                } catch (ServiceException e) {
                    throw new CommandException("unable to close topic", e);
                }
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
