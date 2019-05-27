package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that is used to change topic's {@code hidden} state.
 */
public class ChangeTopicHiddenStateCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String MESSAGE_SUCCESSFULLY_DELETED_PROPERTY = "topic.message_deleted";
    private static final String MESSAGE_DELETION_ERROR_PROPERTY = "topic.message_deletion_error";
    private static final String TOPIC_CHANGE_HIDDEN_STATE_PARAMETER = "hide";

    public ChangeTopicHiddenStateCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
                boolean hide = Boolean.parseBoolean(data.getRequestParameter(TOPIC_CHANGE_HIDDEN_STATE_PARAMETER));
                TopicService topicService = new TopicService();
                try {
                    commandResult.setPage(ApplicationConstants.SHOW_TOPICS);
                    if (topicService.changeTopicHiddenState(topicId, hide)) {
                        logger.log(Level.INFO, "topic hidden state successfully changed, topic id=" + topicId +
                                ", new hidden state: " + hide);
                        data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                                MESSAGE_SUCCESSFULLY_DELETED_PROPERTY);
                    } else {
                        data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                                MESSAGE_DELETION_ERROR_PROPERTY);
                        logger.log(Level.WARN, "unable to change topic hidden state, topicId=" + topicId);
                    }
                } catch (ServiceException e) {
                    throw new CommandException("could not change topic hidden state: topicId=" + topicId, e);
                }
        } catch (NumberFormatException e) {
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
