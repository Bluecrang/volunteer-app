package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.service.MessageService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeleteMessageCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String MESSAGE_SUCCESSFULLY_DELETED_PROPERTY = "topic.message_deleted";
    private static final String MESSAGE_DELETION_ERROR_PROPERTY = "topic.message_deletion_error";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            long messageId = Long.parseLong(data.getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER));
            data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION, MESSAGE_DELETION_ERROR_PROPERTY);
            if (sessionAccountObject instanceof Account) {
                Account sessionAccount = (Account) sessionAccountObject;
                MessageService messageService = new MessageService();
                try {
                    Message message = messageService.findMessageById(messageId);
                    if (message != null) {
                        commandResult.setPage(ApplicationConstants.SHOW_TOPIC_LAST_PAGE + message.getTopic().getTopicId());
                        if (messageService.deleteMessage(sessionAccount, messageId)) {
                            logger.log(Level.INFO, "account id=" + sessionAccount.getAccountId() +
                                    " successfully deleted message id=" + messageId);
                            data.putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION, MESSAGE_SUCCESSFULLY_DELETED_PROPERTY);
                        } else {
                            logger.log(Level.WARN, "could not delete message: accountId=" + sessionAccount.getAccountId() +
                                    ", messageId=" + messageId);
                        }
                    }
                } catch (ServiceException e) {
                    throw new CommandException("account id=" + sessionAccount.getAccountId() +
                            " could not delete message id=" + messageId, e);
                }
            }
        } catch (NumberFormatException e) {
           throw new CommandException("could not parse message id to long value", e);
        }
        return commandResult;
    }
}
