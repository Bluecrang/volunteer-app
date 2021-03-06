package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.service.MessageService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that allows to delete message from the database.
 */
public class DeleteMessageCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String MESSAGE_SUCCESSFULLY_DELETED_PROPERTY = "topic.message_deleted";
    private static final String MESSAGE_DELETION_ERROR_PROPERTY = "topic.message_deletion_error";

    public DeleteMessageCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long messageId = Long.parseLong(data.getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER));
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            MessageService messageService = new MessageService();
            try {
                commandResult.setPage(ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
                if (messageService.deleteMessage(messageId)) {
                    logger.log(Level.INFO, "message with id=" + messageId + " successfully deleted");
                    data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                            MESSAGE_SUCCESSFULLY_DELETED_PROPERTY);
                } else {
                    data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                            MESSAGE_DELETION_ERROR_PROPERTY);
                    logger.log(Level.WARN, "could not delete message: messageId=" + messageId);
                }
            } catch (ServiceException e) {
                throw new CommandException("could not delete message id=" + messageId, e);
            }
        } catch (NumberFormatException e) {
           throw new CommandException("could not parse message id to long value", e);
        }
        return commandResult;
    }
}
