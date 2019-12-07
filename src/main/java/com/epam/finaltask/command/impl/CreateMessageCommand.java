package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.service.MessageService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.TextValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command that is used to create message and add it to the database.
 */
public class CreateMessageCommand extends Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String MESSAGE_TEXT_PARAMETER = "text";
    private static final String MESSAGE_CREATION_ERROR_PROPERTY = "topic.message_creation_error";
    private static final int MAX_MESSAGE_TEXT_LENGTH = 256;
    private static final String MESSAGE_CREATION_ILLEGAL_LENGTH_PROPERTY = "topic.message_creation_illegal_length";

    private TopicService topicService;
    private MessageService messageService;

    public CreateMessageCommand(CommandConstraints constraints) {
        super(constraints);
        this.topicService = new TopicService();
        this.messageService = new MessageService();
    }

    public CreateMessageCommand(CommandConstraints constraints, TopicService topicService, MessageService messageService) {
        super(constraints);
        this.topicService = topicService;
        this.messageService = messageService;
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            CommandResult result = new CommandResult();
            Account sessionAccount = data.getSessionAccount();
            Topic topic = topicService.findTopicById(topicId);
            if ((topic == null || sessionAccount == null) ||
                    (sessionAccount.getAccountId() != topic.getAccount().getAccountId() &&
                            sessionAccount.getAccountType() != AccountType.ADMIN &&
                            sessionAccount.getAccountType() != AccountType.VOLUNTEER)) {
                result.assignTransitionTypeError();
                result.setCode(ApplicationConstants.PAGE_NOT_FOUND_ERROR_CODE);
                return result;
            }
            result.setPage(ApplicationConstants.SHOW_TOPICS);
            String text = data.getRequestParameter(MESSAGE_TEXT_PARAMETER);
            logger.log(Level.DEBUG, "message text: " + text);
            TextValidator textValidator = new TextValidator();
            if (textValidator.validate(text, MAX_MESSAGE_TEXT_LENGTH)) {
                result.setPage(ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
                try {
                    if (messageService.createMessage(sessionAccount, topicId, text)) {
                        logger.log(Level.INFO, "user (id=" + sessionAccount.getAccountId() + ") created message for topic (id="
                                + topicId + ")");
                    } else {
                        logger.log(Level.WARN, "could not create message using given parameters");
                        data.putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                                MESSAGE_CREATION_ERROR_PROPERTY);
                    }
                } catch (ServiceException e) {
                    throw new CommandException("could not create message", e);
                }
            } else {
                logger.log(Level.WARN, "user id=" + sessionAccount.getAccountId() + " could not create message: illegal text length");
                data.putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                        MESSAGE_CREATION_ILLEGAL_LENGTH_PROPERTY);
            }
            return result;
        } catch (NumberFormatException e) {
            throw new CommandException("unable to parse topicId", e);
        } catch (ServiceException e) {
            throw new CommandException("ServiceException while looking for topic", e);
        }
    }
}
