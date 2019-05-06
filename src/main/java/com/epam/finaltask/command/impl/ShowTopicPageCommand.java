package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
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

public class ShowTopicPageCommand implements Command {

    private static final Logger logger = LogManager.getLogger();

    private static final String TOPIC_ID_ATTRIBUTE = "topic_id";
    private static final String MESSAGE_LIST_ATTRIBUTE = "message_list";
    private static final String TOPIC_ATTRIBUTE = "topic";
    private static final int NUMBER_OF_MESSAGES_PER_PAGE = 5;
    private static final String NUMBER_OF_MESSAGES_PER_PAGE_ATTRIBUTE = "messages_per_page";
    private static final String LAST_PAGE = "last";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            TopicService topicService = new TopicService();
            try {
                Topic topic = topicService.findTopicById(topicId);
                data.putRequestAttribute(TOPIC_ATTRIBUTE, topic);
                String currentPageString = data.getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
                MessageService messageService = new MessageService();
                int messageCount = messageService.countMessages(topicId);
                int numberOfPages = Math.toIntExact(Math.round(Math.ceil((double)messageCount / NUMBER_OF_MESSAGES_PER_PAGE))); //todo handle ArithmeticalException
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
                data.putRequestAttribute(TOPIC_ID_ATTRIBUTE, topicId); //todo (remove?) (topic already passed to data)
                data.putRequestAttribute(NUMBER_OF_MESSAGES_PER_PAGE_ATTRIBUTE, NUMBER_OF_MESSAGES_PER_PAGE);
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
