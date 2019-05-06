package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.command.CommandData;
import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.command.CommandResult;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.PageConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ShowTopicsCommand implements Command {

    private static final Logger logger = LogManager.getLogger();
    private static final String TOPIC_LIST_ATTRIBUTE = "topic_list";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            TopicService service = new TopicService();
            List<Topic> topicList = service.findAllTopics();
            logger.log(Level.DEBUG, "number of topics found: " + topicList.size());
            data.putRequestAttribute(TOPIC_LIST_ATTRIBUTE, topicList);
            commandResult.setPage(PageConstants.TOPICS_PAGE);
        } catch (ServiceException e) {
            throw new CommandException("unable to show topics", e);
        }
        return commandResult;
    }
}
