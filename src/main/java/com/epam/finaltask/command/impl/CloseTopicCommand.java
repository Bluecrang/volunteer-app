package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that allows to close topic.
 */
public class CloseTopicCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String COULD_NOT_CLOSE_TOPIC_ERROR = "topic.could_not_close_topic_error";
    private static final String TOPIC_CLOSED = "topic.topic_closed_successfully";

    public CloseTopicCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            commandResult.setPage(ApplicationConstants.SHOW_TOPIC + topicId);
            TopicService topicService = new TopicService();

            if (topicService.closeTopic(topicId)) {
                logger.log(Level.INFO, "topic with id=" + topicId + " closed");
                data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE, TOPIC_CLOSED);
            } else {
                data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                        COULD_NOT_CLOSE_TOPIC_ERROR);
                logger.log(Level.WARN, "could not close topic id=" + topicId);
            }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        } catch (ServiceException e) {
            throw new CommandException("unable to close topic", e);
        }
        return commandResult;
    }
}
