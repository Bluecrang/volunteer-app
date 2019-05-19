package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.MessageService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.TextValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateMessageCommand implements Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String MESSAGE_TEXT_PARAMETER = "text";
    private static final String MESSAGE_CREATION_ERROR_PROPERTY = "topic.message_creation_error";
    private static final int MAX_MESSAGE_TEXT_LENGTH = 256;
    private static final String MESSAGE_CREATION_ILLEGAL_LENGTH_PROPERTY = "topic.message_creation_illegal_length";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object object = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult result = new CommandResult();
        result.setPage(ApplicationConstants.SHOW_TOPICS);
        if (object instanceof Account) {
            Account account = (Account) object;
            String text = data.getRequestParameter(MESSAGE_TEXT_PARAMETER);
            TextValidator textValidator = new TextValidator();
            if (textValidator.validate(text, MAX_MESSAGE_TEXT_LENGTH)) {
                long topicId = Long.parseLong(data.getRequestParameter((ApplicationConstants.TOPIC_ID_PARAMETER)));
                result.setPage(ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
                MessageService messageService = new MessageService();
                try {
                    if (messageService.createMessage(account, topicId, text)) {
                        logger.log(Level.INFO, "user (id=" + account.getAccountId() + ") created message for topic (id="
                                + topicId + ")");
                    } else {
                        logger.log(Level.WARN, "could not create message using given parameters");
                        data.putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION,
                                MESSAGE_CREATION_ERROR_PROPERTY);
                    }
                } catch (ServiceException e) {
                    throw new CommandException("could not create message", e);
                }
            } else {
                logger.log(Level.WARN, "user id=" + account.getAccountId() + " could not create message: illegal text length");
                data.putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION,
                        MESSAGE_CREATION_ILLEGAL_LENGTH_PROPERTY);
            }
        }
        return result;
    }
}
