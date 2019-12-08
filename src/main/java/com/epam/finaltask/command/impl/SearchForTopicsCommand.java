package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
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

import java.util.Collections;
import java.util.List;

/**
 * Command that is used to search for topics using title substring.
 */
public class SearchForTopicsCommand extends Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String TOPIC_LIST_ATTRIBUTE = "topic_list";
    private static final String SEARCH_STRING_PARAMETER = "text";
    private static final String NO_TOPICS_FOUND = "topics.no_topics_found";

    private TopicService topicService;

    public SearchForTopicsCommand(CommandConstraints constraints) {
        super(constraints);
        this.topicService = new TopicService();
    }

    public SearchForTopicsCommand(CommandConstraints constraints, TopicService topicService) {
        super(constraints);
        this.topicService = topicService;
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        commandResult.setPage(PageConstants.TOPICS_PAGE);
        try {
            Account sessionAccount = data.getSessionAccount();
            if (sessionAccount == null || sessionAccount.getAccountType() == AccountType.GUEST) {
                data.putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE,
                        ApplicationConstants.ACCOUNT_TYPE_GUEST_NOTIFICATION_KEY);
                return commandResult;
            }
            String regex = data.getRequestParameter(SEARCH_STRING_PARAMETER);
            List<Topic> topicList = topicService.findTopicsByTitleSubstring(sessionAccount, regex);
            if (sessionAccount.getAccountType() != AccountType.ADMIN) {
                topicList.removeIf(Topic::isHidden);
            }
            logger.log(Level.DEBUG, "number of found topics: " + topicList.size());
            if (!topicList.isEmpty()) {
                data.putRequestAttribute(TOPIC_LIST_ATTRIBUTE, topicList);
            } else {
                data.putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE, NO_TOPICS_FOUND);
            }
        } catch (ServiceException e) {
            throw new CommandException("unable to show topics", e);
        }
        return commandResult;
    }
}
