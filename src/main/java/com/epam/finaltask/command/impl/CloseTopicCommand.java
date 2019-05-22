package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.AdministratorValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command which allows to close topic.
 */
public class CloseTopicCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String COULD_NOT_CLOSE_TOPIC_ERROR = "topic.could_not_close_topic_error";
    private static final String TOPIC_CLOSED = "topic.topic_closed_successfully";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult commandResult = new CommandResult();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_TOPIC + topicId);
            AdministratorValidator validator = new AdministratorValidator();
            if (validator.validate(sessionAccountObject)) {
                try {
                    TopicService topicService = new TopicService();
                    if (topicService.closeTopic(topicId)) {
                        logger.log(Level.INFO, "topic with id=" + topicId + " closed");
                        data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE, TOPIC_CLOSED);
                    } else {
                        data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                                COULD_NOT_CLOSE_TOPIC_ERROR);
                        logger.log(Level.WARN, "could not close topic id=" + topicId);
                    }
                } catch (ServiceException e) {
                    throw new CommandException("unable to close topic", e);
                }
            } else { //todo validator
            data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                    COULD_NOT_CLOSE_TOPIC_ERROR);
            logger.log(Level.WARN, "could not close topic: closing account is not an administrator. topicId=" + topicId);
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
