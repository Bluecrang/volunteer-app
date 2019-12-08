package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.service.MessageService;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.PageConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowTopicPageCommandTest {

    @Mock
    private TopicService topicService;
    @Mock
    private MessageService messageService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ShowTopicPageCommand showTopicPageCommand;

    private String topicId = "1";
    private long longTopicId = 1;
    private String page = "1";
    private int intPage = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validData_topicShown() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(1));
        messages.add(new Message(2));
        messages.add(new Message(3));
        when(messageService.countMessages(longTopicId))
                .thenReturn(messages.size());
        when(messageService.findTopicPageMessages(longTopicId, intPage, 5))
                .thenReturn(messages);

        CommandResult actual = showTopicPageCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.TOPIC_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicById(longTopicId);
        verify(messageService).countMessages(longTopicId);
        verify(messageService).findTopicPageMessages(longTopicId, intPage, 5);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPIC_PAGE_COUNT_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, 5);
    }

    @Test
    public void performAction_topicHiddenUserNotAdmin_404ErrorCodeSet() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.USER);
        Topic topic = new Topic(longTopicId);
        topic.setHidden(true);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);

        CommandResult actual = showTopicPageCommand.performAction(commandData);

        Assert.assertEquals(actual.getCode(), 404);
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicById(longTopicId);
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);
        when(messageService.countMessages(longTopicId))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            showTopicPageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicById(longTopicId);
        verify(messageService).countMessages(longTopicId);
    }

    @Test
    public void performAction_unparsableTopicId_commandException() {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn("qeqwvebr");

        Assert.assertThrows(CommandException.class, () -> {
            showTopicPageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
    }

    @Test
    public void performAction_unparsablePageParameter_commandException() throws ServiceException {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        Topic topic = new Topic(longTopicId);
        topic.setAccount(account);
        when(commandData.getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER))
                .thenReturn(topicId);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn("vqveqv");
        when(topicService.findTopicById(longTopicId))
                .thenReturn(topic);
        when(messageService.countMessages(longTopicId))
                .thenReturn(15);

        Assert.assertThrows(CommandException.class, () -> {
            showTopicPageCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.TOPIC_ID_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicById(longTopicId);
        verify(messageService).countMessages(longTopicId);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
    }
}
