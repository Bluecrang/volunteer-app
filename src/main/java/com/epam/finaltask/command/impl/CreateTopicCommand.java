package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.validation.TextValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateTopicCommand implements Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String TOPIC_TITLE_PARAMETER = "title";
    private static final String TOPIC_TEXT_PARAMETER = "text";
    private static final String TOPIC_CREATION_MESSAGE = "topic_creation_message";
    private static final String TOPIC_EXISTS_PROPERTY = "topics.topic_exists";
    private static final String TOPIC_CREATED_PROPERTY = "topics.topic_created";
    private static final String TOPIC_CREATION_ILLEGAL_PARAMETERS_PROPERTY = "topics.illegal_topic_creation_parameters";
    private static final int MAX_TOPIC_TITLE_LENGTH = 100;
    private static final int MAX_TOPIC_MESSAGE_LENGTH = 400;
    private static final String TOPIC_CREATION_ILLEGAL_TITLE_OR_TEXT_PROPERTY = "topics.illegal_title_or_text_length";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object object = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult result = new CommandResult();
        result.assignTransitionTypeForward();
        result.setPage(ApplicationConstants.SHOW_TOPICS);
        if (object instanceof Account) {
            Account account = (Account) object;
            String title = data.getRequestParameter(TOPIC_TITLE_PARAMETER);
            String text = data.getRequestParameter(TOPIC_TEXT_PARAMETER);
            if (validateTopicData(title, text)) {
                TopicService topicService = new TopicService();
                try {
                    if (topicService.findTopicByTitle(title) == null) {
                        try {
                            if (topicService.createTopic(account, title, text)) {
                                logger.log(Level.INFO, "user (id=" + account.getAccountId() + ") created topic (title=" + title +
                                        ")");
                                data.putRequestAttribute(TOPIC_CREATION_MESSAGE, TOPIC_CREATED_PROPERTY);
                            } else {
                                data.putRequestAttribute(TOPIC_CREATION_MESSAGE, TOPIC_CREATION_ILLEGAL_PARAMETERS_PROPERTY);
                            }
                        } catch (ServiceException e) {
                            throw new CommandException("could not create new topic", e);
                        }
                    } else {
                        logger.log(Level.INFO, "user (id=" + account.getAccountId() + ") could not create topic (title=" + title +
                                "), topic title already taken");
                        data.putRequestAttribute(TOPIC_CREATION_MESSAGE, TOPIC_EXISTS_PROPERTY);
                    }
                } catch (ServiceException e) {
                    throw new CommandException("could not find topic", e);
                }
            } else {
                logger.log(Level.WARN, "user id=" + account.getAccountId() + " could not create topic: title or text length is illegal");
                data.putRequestAttribute(TOPIC_CREATION_MESSAGE, TOPIC_CREATION_ILLEGAL_TITLE_OR_TEXT_PROPERTY);
            }
        }
        return result;
    }

    private boolean validateTopicData(String title, String text) {
        TextValidator textValidator = new TextValidator();
        return (textValidator.validate(title, MAX_TOPIC_TITLE_LENGTH) && textValidator.validate(text, MAX_TOPIC_MESSAGE_LENGTH));
    }
}
