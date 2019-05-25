package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.*;
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
 * Command which is used to show all topics.
 */
public class ShowTopicsCommand extends Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String TOPIC_LIST_ATTRIBUTE = "topic_list";
    private static final String NO_TOPICS = "topics.no_topics";

    public ShowTopicsCommand(CommandConstraints constraints) {
        super(constraints);
    }

    @Override
    public CommandResult performAction(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            TopicService service = new TopicService();
            List<Topic> topicList = service.findAllTopics();
            logger.log(Level.DEBUG, "number of found topics: " + topicList.size());
            if (!topicList.isEmpty()) {
                data.putRequestAttribute(TOPIC_LIST_ATTRIBUTE, topicList);
            } else {
                data.putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE, NO_TOPICS);
            }
            commandResult.setPage(PageConstants.TOPICS_PAGE);
        } catch (ServiceException e) {
            throw new CommandException("unable to show topics", e);
        }
        return commandResult;
    }
}
