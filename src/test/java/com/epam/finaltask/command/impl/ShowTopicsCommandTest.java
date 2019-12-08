package com.epam.finaltask.command.impl;

import com.epam.finaltask.command.CommandException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Topic;
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

public class ShowTopicsCommandTest {

    @Mock
    private TopicService topicService;
    @Mock
    private CommandData commandData;
    @InjectMocks
    private ShowTopicsCommand showTopicsCommand;

    private String page = "1";
    private int intPage = 1;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void performAction_validDataUserAdmin_topicsShown() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic(1));
        topics.add(new Topic(2));
        Topic hiddenTopic = new Topic(3);
        hiddenTopic.setHidden(true);
        topics.add(hiddenTopic);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findPageTopics(intPage, 10, true))
                .thenReturn(topics);
        when(topicService.countTopics(true))
                .thenReturn(3);

        CommandResult actual = showTopicsCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.TOPICS_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).countTopics(true);
        verify(topicService).findPageTopics(intPage, 10, true);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_PAGE_COUNT_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_CURRENT_PAGE_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, 5);
        verify(commandData).putRequestAttribute("topic_list", topics);
    }

    @Test
    public void performAction_userAdminPageParameterUnparsable_topicsFirstPageShown() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic(1));
        topics.add(new Topic(2));
        Topic hiddenTopic = new Topic(3);
        hiddenTopic.setHidden(true);
        topics.add(hiddenTopic);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn("egqegq");
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findPageTopics(1, 10, true))
                .thenReturn(topics);
        when(topicService.countTopics(true))
                .thenReturn(3);

        CommandResult actual = showTopicsCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.TOPICS_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).countTopics(true);
        verify(topicService).findPageTopics(1, 10, true);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_PAGE_COUNT_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_CURRENT_PAGE_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, 5);
        verify(commandData).putRequestAttribute("topic_list", topics);
    }

    @Test
    public void performAction_userAdminServiceException_commandException() throws ServiceException {
        Account account = new Account();
        account.setAccountType(AccountType.ADMIN);
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic(1));
        topics.add(new Topic(2));
        Topic hiddenTopic = new Topic(3);
        hiddenTopic.setHidden(true);
        topics.add(hiddenTopic);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn("egqegq");
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findPageTopics(1, 10, true))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            showTopicsCommand.performAction(commandData);
        });
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).findPageTopics(1, 10, true);
    }

    @Test
    public void performAction_validDataUserVolunteer_notHiddenTopicsShown() throws ServiceException, CommandException {
        Account account = new Account();
        account.setAccountType(AccountType.VOLUNTEER);
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic(1));
        topics.add(new Topic(2));
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findPageTopics(intPage, 10, false))
                .thenReturn(topics);
        when(topicService.countTopics(false))
                .thenReturn(2);

        CommandResult actual = showTopicsCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.TOPICS_PAGE);
        verify(commandData).getRequestParameter(ApplicationConstants.PAGE_PARAMETER);
        verify(commandData).getSessionAccount();
        verify(topicService).countTopics(false);
        verify(topicService).findPageTopics(intPage, 10, false);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_PAGE_COUNT_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_CURRENT_PAGE_ATTRIBUTE, 1);
        verify(commandData).putRequestAttribute(ApplicationConstants.PAGE_STEP_ATTRIBUTE, 5);
        verify(commandData).putRequestAttribute("topic_list", topics);
    }

    @Test
    public void performAction_validDataUserRoleUser_personalTopicsShown() throws ServiceException, CommandException {
        long accountId = 1;
        Account account = new Account(accountId);
        account.setAccountType(AccountType.USER);
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic(1));
        topics.add(new Topic(2));
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicsByAuthorId(accountId))
                .thenReturn(topics);

        CommandResult actual = showTopicsCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.TOPICS_PAGE);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicsByAuthorId(accountId);
        verify(commandData).putRequestAttribute("topic_list", topics);
    }

    @Test
    public void performAction_validDataUserRoleUserNoTopics_topicsNotFoundAttributeSet() throws ServiceException, CommandException {
        long accountId = 1;
        Account account = new Account(accountId);
        account.setAccountType(AccountType.USER);
        List<Topic> topics = new ArrayList<>();
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicsByAuthorId(accountId))
                .thenReturn(topics);

        CommandResult actual = showTopicsCommand.performAction(commandData);

        Assert.assertEquals(actual.getPage(), PageConstants.TOPICS_PAGE);
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicsByAuthorId(accountId);
        verify(commandData).putRequestAttribute(ApplicationConstants.TOPICS_MESSAGE_ATTRIBUTE, "topics.no_topics");
    }

    @Test
    public void performAction_validDataUserRoleUserServiceException_commandException() throws ServiceException {
        long accountId = 1;
        Account account = new Account(accountId);
        account.setAccountType(AccountType.USER);
        when(commandData.getRequestParameter(ApplicationConstants.PAGE_PARAMETER))
                .thenReturn(page);
        when(commandData.getSessionAccount())
                .thenReturn(account);
        when(topicService.findTopicsByAuthorId(accountId))
                .thenThrow(new ServiceException());

        Assert.assertThrows(CommandException.class, () -> {
            showTopicsCommand.performAction(commandData);
        });
        verify(commandData).getSessionAccount();
        verify(topicService).findTopicsByAuthorId(accountId);
    }
}
