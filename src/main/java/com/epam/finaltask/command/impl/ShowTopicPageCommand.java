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
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command that is used to show topic page.
 */
public class ShowTopicPageCommand extends Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String MESSAGE_LIST_ATTRIBUTE = "message_list";
    private static final String TOPIC_ATTRIBUTE = "topic";
    private static final int NUMBER_OF_MESSAGES_PER_PAGE = 5;
    private static final String LAST_PAGE = "last";
    private static final Integer PAGE_STEP = 5;

    public ShowTopicPageCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            TopicService topicService = new TopicService();
            try {
                Topic topic = topicService.findTopicById(topicId);
                Account sessionAccount = data.getSessionAccount();
                if ((topic == null || sessionAccount == null) ||
                        (sessionAccount.getAccountId() != topic.getAccount().getAccountId() &&
                                sessionAccount.getAccountType() != AccountType.ADMIN &&
                                sessionAccount.getAccountType() != AccountType.VOLUNTEER)) {
                    commandResult.assignTransitionTypeError();
                    commandResult.setCode(ApplicationConstants.PAGE_NOT_FOUND_ERROR_CODE);
                    logger.log(Level.WARN, "cannot show topic page, " +
                            "topic not found or session account does not have rights. Topic id=" + topicId);
                    return commandResult;
                }
                if (topic.isHidden() && sessionAccount.getAccountType() != AccountType.ADMIN) {
                    logger.log(Level.WARN, "cannot show topic page, topic is hidden. Topic id=" + topicId);
                    commandResult.assignTransitionTypeError();
                    commandResult.setCode(ApplicationConstants.PAGE_NOT_FOUND_ERROR_CODE);
                    return commandResult;
                }
                data.putRequestAttribute(TOPIC_ATTRIBUTE, topic);
                commandResult.assignTransitionTypeForward();
                String currentPageString = data.getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
                MessageService messageService = new MessageService();
                int messageCount = messageService.countMessages(topicId);
                int numberOfPages = Math.toIntExact(Math.round(Math.ceil((double)messageCount / NUMBER_OF_MESSAGES_PER_PAGE)));
                int currentPage;
                if (currentPageString.equals(LAST_PAGE)) {
                    currentPage = (numberOfPages != 0) ? numberOfPages : 1;
                } else {
                    currentPage = Integer.parseInt(data.getRequestParameter(ApplicationConstants.PAGE_PARAMETER));
                }
                data.putRequestAttribute(ApplicationConstants.TOPIC_CURRENT_PAGE_ATTRIBUTE, currentPage);
                List<Message> messageList = messageService.findTopicPageMessages(topicId, currentPage, NUMBER_OF_MESSAGES_PER_PAGE);
                logger.log(Level.DEBUG, "messageList size=" + messageList.size());
                logger.log(Level.DEBUG, "number of pages: " + numberOfPages);
                data.putRequestAttribute(MESSAGE_LIST_ATTRIBUTE, messageList);
                data.putRequestAttribute(ApplicationConstants.TOPIC_PAGE_COUNT_ATTRIBUTE, numberOfPages);
                data.putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, PAGE_STEP);
                commandResult.setPage(PageConstants.TOPIC_PAGE);
            } catch (ServiceException e) {
                throw new CommandException("Unable to show topic page: ServiceException has occurred", e);
            }
        } catch (NumberFormatException e) {
            throw new CommandException("Unable to show topic page: could not parse parameter", e);
        }
        return commandResult;
    }
}
