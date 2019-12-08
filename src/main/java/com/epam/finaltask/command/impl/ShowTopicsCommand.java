package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Command that is used to show all topics or several from the specific page.
 * If account type is {@link AccountType#ADMIN} or {@link AccountType#VOLUNTEER}, then topics will be paginated.
 * If account type is  {@link AccountType#USER}, then all topics created by user will be shown.
 */
public class ShowTopicsCommand extends Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String TOPIC_LIST_ATTRIBUTE = "topic_list";
    private static final String NO_TOPICS = "topics.no_topics";
    private static final int NUMBER_OF_TOPICS_PER_PAGE = 10;
    private static final int DEFAULT_CURRENT_PAGE = 1;
    private static final Integer PAGE_STEP = 5;

    private TopicService topicService;

    public ShowTopicsCommand(CommandConstraints constraints) {
        super(constraints);
        this.topicService = new TopicService();
    }

    public ShowTopicsCommand(CommandConstraints constraints, TopicService topicService) {
        super(constraints);
        this.topicService = topicService;
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.setPage(PageConstants.TOPICS_PAGE);
        commandResult.assignTransitionTypeForward();
        try {
            Account sessionAccount = data.getSessionAccount();
            if (sessionAccount == null || sessionAccount.getAccountType() == AccountType.GUEST) {
                data.putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE,
                        ApplicationConstants.ACCOUNT_TYPE_GUEST_NOTIFICATION_KEY);
                return commandResult;
            }
            AccountType sessionAccountType = sessionAccount.getAccountType();
            long sessionAccountId = sessionAccount.getAccountId();
            List<Topic> topicList;
            if (sessionAccountType == AccountType.ADMIN ||
                    sessionAccountType == AccountType.VOLUNTEER) {
                int currentPage;
                try {
                    currentPage = Integer.parseInt(data.getRequestParameter(ApplicationConstants.PAGE_PARAMETER));
                } catch (NumberFormatException e) {
                    currentPage = DEFAULT_CURRENT_PAGE;
                }
                boolean showHidden = false;
                if (sessionAccountType == AccountType.ADMIN) {
                    showHidden = true;
                }
                topicList = topicService.findPageTopics(currentPage, NUMBER_OF_TOPICS_PER_PAGE, showHidden);
                logger.log(Level.DEBUG, "topic list: " + topicList);
                int topicCount = topicService.countTopics(showHidden);
                int numberOfPages = Math.toIntExact(Math.round(Math.ceil((double)topicCount / NUMBER_OF_TOPICS_PER_PAGE)));
                data.putRequestAttribute(ApplicationConstants.TOPICS_PAGE_COUNT_ATTRIBUTE, numberOfPages);
                data.putRequestAttribute(ApplicationConstants.TOPICS_CURRENT_PAGE_ATTRIBUTE, currentPage);
                data.putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, PAGE_STEP);
            } else {
                topicList = topicService.findTopicsByAuthorId(sessionAccountId);
            }
            logger.log(Level.DEBUG, "number of found topics: " + topicList.size());
            if (!topicList.isEmpty()) {
                logger.log(Level.INFO, "topics list provided to account id=" + sessionAccountId);
                data.putRequestAttribute(TOPIC_LIST_ATTRIBUTE, topicList);
            } else {
                logger.log(Level.INFO, "no topics found for account id=" + sessionAccountId);
                data.putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE, NO_TOPICS);
            }
        } catch (ServiceException e) {
            throw new CommandException("unable to show topics", e);
        }
        return commandResult;
    }
}
