package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.service.ServiceException;
import com.epam.finaltask.service.TopicService;
import com.epam.finaltask.util.ApplicationConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class SearchForTopicsCommandTest {

    @Mock
    private TopicService topicService;
    private CommandData commandData;
    @InjectMocks
    private SearchForTopicsCommand searchForTopicsCommand;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        commandData = spy(CommandData.class);
    }

    @Test
    public void performAction_accountNull_noTopics() throws CommandException {
        when(commandData.getSessionAccount())
                .thenReturn(null);

        searchForTopicsCommand.performAction(commandData);

        Assert.assertEquals(commandData.getRequestAttribute("topic_list"), null);
        verify(commandData).getSessionAccount();
    }

    @Test
    public void performAction_accountUser_topicsFound() throws ServiceException, CommandException {
        String regex = "regex";
        Account account = new Account();
        account.setAccountType(AccountType.USER);
        List<Topic> topics = new ArrayList<Topic>() {
            {
                add(new Topic(1));
                Topic topic = new Topic(2);
                topic.setHidden(true);
                add(topic);
                add(new Topic(3));
            }
        };
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(regex);
        when(topicService.findTopicsByTitleSubstring(account, regex))
                .thenReturn(topics);

        searchForTopicsCommand.performAction(commandData);

        Assert.assertEquals(((List<Topic>)commandData.getRequestAttribute("topic_list")).size(), 2);
        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicsByTitleSubstring(account, regex);
    }

    @Test
    public void performAction_accountAdmin_hiddenTopicsFound() throws ServiceException, CommandException {
        String regex = "regex";
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        List<Topic> topics = new ArrayList<Topic>() {
            {
                add(new Topic(1));
                Topic topic = new Topic(2);
                topic.setHidden(true);
                add(topic);
                add(new Topic(3));
            }
        };
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(regex);
        when(topicService.findTopicsByTitleSubstring(account, regex))
                .thenReturn(topics);

        searchForTopicsCommand.performAction(commandData);

        Assert.assertEquals(((List<Topic>)commandData.getRequestAttribute("topic_list")).size(), 3);
        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicsByTitleSubstring(account, regex);
    }

    @Test
    public void performAction_noTopicsFound_topicsNotFoundMessageSet() throws ServiceException, CommandException {
        String regex = "regex";
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(regex);
        when(topicService.findTopicsByTitleSubstring(account, regex))
                .thenReturn(Collections.emptyList());

        searchForTopicsCommand.performAction(commandData);

        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicsByTitleSubstring(account, regex);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE, "topics.no_topics_found");
    }

    @Test
    public void performAction_serviceException_commandException() throws ServiceException {
        String regex = "regex";
        Account account = new Account();
        account.setAccountType(AccountType.USER);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(commandData.getRequestParameter("text"))
                .thenReturn(regex);
        when(topicService.findTopicsByTitleSubstring(account, regex))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            searchForTopicsCommand.performAction(commandData);
        });

        verify(commandData).getSessionAccount();
        verify(commandData).getRequestParameter("text");
        verify(topicService).findTopicsByTitleSubstring(account, regex);
    }
}
