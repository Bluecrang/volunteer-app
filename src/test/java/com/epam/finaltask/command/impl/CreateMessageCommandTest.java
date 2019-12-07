package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.service.MessageService;
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

public class CreateMessageCommandTest {

    @Mock
    private TopicService topicService;
    @Mock
    private MessageService messageService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private CreateMessageCommand createMessageCommand;

    private String topicId = "1";
    private long longTopicId = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_messageCreated() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String text = "text";
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);
        when(messageService.createMessage(account, longTopicId, text))
                .thenReturn(true);

        CommandResult actual = createMessageCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicById(longTopicId);
        verify(messageService).createMessage(account, longTopicId, text);
    }

    @Test
    public void performAction_messageCannotBeCreated_errorMessageSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String text = "text";
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);
        when(messageService.createMessage(account, longTopicId, text))
                .thenReturn(false);

        CommandResult actual = createMessageCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), ApplicationConstants.SHOW_TOPIC_LAST_PAGE + topicId);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicById(longTopicId);
        verify(messageService).createMessage(account, longTopicId, text);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPIC_ACTION_NOTIFICATION_ATTRIBUTE,
                "topic.message_creation_error");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        String text = "text";
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(text);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);
        when(messageService.createMessage(account, longTopicId, text))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            createMessageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicById(longTopicId);
        verify(messageService).createMessage(account, longTopicId, text);
    }

    @Test
    public void performAction_unparsableTopicId_commandException() throws ServiceException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn("fqefq");

        Assert.assertThrows(CommandException.class, () -> {
            createMessageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
    }

    @Test
    public void performAction_topicDoesNotExist_pageNotFoundError() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(null);

        CommandResult actual = createMessageCommand.performAction(commandData);

        Assert.assertEquals(actual.getCode(), 404);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicById(longTopicId);
    }

    @Test
    public void performAction_userHasUserRole_pageNotFoundError() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.USER);
        Topic topic = new Topic(longTopicId);
        Account topicAccount = new Account(135);
        topicAccount.setAccountType(AccountType.ADMIN);
        topic.setAccount(topicAccount);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);

        CommandResult actual = createMessageCommand.performAction(commandData);

        Assert.assertEquals(actual.getCode(), 404);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicById(longTopicId);
    }
}
