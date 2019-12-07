package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class CreateTopicCommandTest {

    @Mock
    private TopicService topicService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private CreateTopicCommand createTopicCommand;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_topicCreationMessageSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String title = "title";
        String text = "text";
        when(commandData.getRequestParameter("title"))
                .thenReturn(title);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.createTopic(account, title, text))
                .thenReturn(true);

        CommandResult actual = createTopicCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPICS);
        verify(commandData).getRequestParameter("title");
        verify(commandData).getRequestParameter("text");
        verify(topicService).createTopic(account, title, text);
        verify(commandData).getSessionAccount();
        verify(commandData).putRequestAttribute("topic_creation_message", "topics.topic_created");
    }

    @Test
    public void performAction_topicNotCreated_topicCreationErrorMessageSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String title = "title";
        String text = "text";
        when(commandData.getRequestParameter("title"))
                .thenReturn(title);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.createTopic(account, title, text))
                .thenReturn(false);

        CommandResult actual = createTopicCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPICS);
        verify(commandData).getRequestParameter("title");
        verify(commandData).getRequestParameter("text");
        verify(topicService).createTopic(account, title, text);
        verify(commandData).getSessionAccount();
        verify(commandData).putRequestAttribute("topic_creation_message", "topics.illegal_topic_creation_parameters");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String title = "title";
        String text = "text";
        when(commandData.getRequestParameter("title"))
                .thenReturn(title);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.createTopic(account, title, text))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            createTopicCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter("title");
        verify(commandData).getRequestParameter("text");
        verify(commandData).getSessionAccount();
        verify(topicService).createTopic(account, title, text);
    }

    @Test
    public void performAction_titleEmpty_errorMessageSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String title = "";
        String text = "text";
        when(commandData.getRequestParameter("title"))
                .thenReturn(title);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(commandData.getSessionAccount())
                .thenReturn(account);

        CommandResult actual = createTopicCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPICS);
        verify(commandData).getRequestParameter("title");
        verify(commandData).getRequestParameter("text");
        verify(commandData).getSessionAccount();
        verify(topicService, never()).createTopic(account, title, text);
        verify(commandData).putRequestAttribute("topic_creation_message", "topics.illegal_title_or_text_length");
    }
}
