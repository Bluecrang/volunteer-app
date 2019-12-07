package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CloseTopicCommandTest {

    @Mock
    private TopicService topicService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private CloseTopicCommand closeTopicCommand;

    private String topicId = "1";
    private long longTopicId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_topicClosed_successMessageSet() throws ServiceException, CommandException {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(topicService.closeTopic(longTopicId))
                .thenReturn(true);

        CommandResult actual = closeTopicCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPIC + topicId);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(topicService).closeTopic(longTopicId);
        verify(commandData).putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                "topic.topic_closed_successfully");
    }

    @Test
    public void performAction_topicNotClosed_failureMessageSet() throws ServiceException, CommandException {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(topicService.closeTopic(longTopicId))
                .thenReturn(false);

        CommandResult actual = closeTopicCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPIC + topicId);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(topicService).closeTopic(longTopicId);
        verify(commandData).putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                "topic.could_not_close_topic_error");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(topicService.changeTopicHiddenState(longTopicId, true))
                .thenThrow(new ServiceException());
        when(topicService.closeTopic(longTopicId))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            closeTopicCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(topicService).closeTopic(longTopicId);
    }

    @Test
    public void performAction_unparsableTopicId_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn("aefa");

        Assert.assertThrows(CommandException.class, () -> {
            closeTopicCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
    }
}
