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

public class ChangeTopicHiddenStateCommandTest {

    @Mock
    private TopicService topicService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ChangeTopicHiddenStateCommand changeTopicHiddenStateCommand;

    private String topicId = "1";
    private long longTopicId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_hiddenStateChanged_successNotificationSet() throws ServiceException, CommandException {
        String hide = "true";
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter("hide"))
                .thenReturn(hide);
        when(topicService.changeTopicHiddenState(longTopicId, true))
                .thenReturn(true);

        CommandResult actual = changeTopicHiddenStateCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPICS);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter("hide");
        verify(topicService).changeTopicHiddenState(longTopicId, true);
        verify(commandData).putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                "topic.hide.change_success");
    }

    @Test
    public void performAction_hiddenStateNotChanged_failureNotificationSet() throws ServiceException, CommandException {
        String hide = "true";
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter("hide"))
                .thenReturn(hide);
        when(topicService.changeTopicHiddenState(longTopicId, true))
                .thenReturn(false);

        CommandResult actual = changeTopicHiddenStateCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPICS);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter("hide");
        verify(topicService).changeTopicHiddenState(longTopicId, true);
        verify(commandData).putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                "topic.hide.change_error");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        String hide = "true";
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter("hide"))
                .thenReturn(hide);
        when(topicService.changeTopicHiddenState(longTopicId, true))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            changeTopicHiddenStateCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter("hide");
        verify(topicService).changeTopicHiddenState(longTopicId, true);
    }

    @Test
    public void performAction_unparsableTopicId_commandException() {
        String hide = "true";
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn("aefa");
        when(commandData.getRequestParameter("hide"))
                .thenReturn(hide);

        Assert.assertThrows(CommandException.class, () -> {
            changeTopicHiddenStateCommand.performAction(commandData);
        });

        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
    }
}
