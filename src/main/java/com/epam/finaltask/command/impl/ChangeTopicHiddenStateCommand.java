package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.Command;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChangeTopicHiddenStateCommand implements Command {


    private static final Logger logger = LogManager.getLogger();

    private static final String MESSAGE_SUCCESSFULLY_DELETED_PROPERTY = "topic.message_deleted";
    private static final String MESSAGE_DELETION_ERROR_PROPERTY = "topic.message_deletion_error";
    private static final String TOPIC_CHANGE_HIDDEN_STATE_PARAMETER = "hide";

    @Override
    public CommandResult execute(CommandData data) throws CommandException {
        Object sessionAccountObject = data.getSessionAttribute(ApplicationConstants.ACCOUNT_ATTRIBUTE);
        CommandResult commandResult = new CommandResult();
        commandResult.assignTransitionTypeForward();
        try {
            long topicId = Long.parseLong(data.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER));
            data.putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION, MESSAGE_DELETION_ERROR_PROPERTY);
            if (sessionAccountObject instanceof Account) {
                Account sessionAccount = (Account) sessionAccountObject;
                boolean hide = Boolean.parseBoolean(data.getRequestParameter(TOPIC_CHANGE_HIDDEN_STATE_PARAMETER));
                TopicService topicService = new TopicService();
                try {
                    if (topicService.changeTopicHiddenState(sessionAccount, topicId, hide)) {
                        logger.log(Level.INFO, "account id=" + sessionAccount +
                                " has successfully changed hidden state of topic id=" + topicId + " to " + hide);
                        data.putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION, MESSAGE_SUCCESSFULLY_DELETED_PROPERTY);
                    } else {
                        throw new CommandException("could not hide topic: accountId=" + sessionAccount.getAccountId() +
                                ", topicId=" + topicId);
                    }
                } catch (ServiceException e) {
                    throw new CommandException("account id=" + sessionAccount.getAccountId() +
                            " could not hide topic id=" + topicId, e);
                }
            }
        } catch (NumberFormatException e) { //TODO NumberFormatException catch
            throw new CommandException("could not parse topic id to long value", e);
        }
        return commandResult;
    }
}
