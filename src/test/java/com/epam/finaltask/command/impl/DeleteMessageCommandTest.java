package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.service.MessageService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteMessageCommandTest {

    @Mock
    private MessageService messageService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private DeleteMessageCommand deleteMessageCommand;

    private String topicId = "1";
    private String messageId = "1";
    private long longMessageId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_messageDeletedNotificationSet() throws ServiceException, CommandException {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER))
                .thenReturn(messageId);
        when(messageService.deleteMessage(longMessageId))
                .thenReturn(true);

        CommandResult actual = deleteMessageCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER);
        verify(messageService).deleteMessage(longMessageId);
        verify(commandData).putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE, "topic.message_deleted");
    }

    @Test
    public void performAction_invalidData_messageDeletionErrorNotificationSet() throws ServiceException, CommandException {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER))
                .thenReturn(messageId);
        when(messageService.deleteMessage(longMessageId))
                .thenReturn(false);

        CommandResult actual = deleteMessageCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER);
        verify(messageService).deleteMessage(longMessageId);
        verify(commandData).putSessionAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                "topic.message_deletion_error");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException, CommandException {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER))
                .thenReturn(messageId);
        when(messageService.deleteMessage(longMessageId))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            deleteMessageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER);
        verify(messageService).deleteMessage(longMessageId);
    }

    @Test
    public void performAction_unparsableMessageId_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER))
                .thenReturn("aefafevbr");

        Assert.assertThrows(CommandException.class, () -> {
            deleteMessageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.MESSAGE_ID_PARAMETER);
    }

    @Test
    public void performAction_unparsableTopicId_commandException() {
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn("aefaefa");

        Assert.assertThrows(CommandException.class, () -> {
            deleteMessageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
    }
}
