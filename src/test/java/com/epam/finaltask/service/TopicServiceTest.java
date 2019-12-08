package com.epam.finaltask.service;

import com.epam.finaltask.dao.*;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class TopicServiceTest {

    private TopicService topicService;
    @Mock
    private TopicDao topicDao;
    @Mock
    private AbstractConnectionManager connectionManager;
    @Mock
    private DaoFactory daoFactory;
    @Mock
    private ConnectionManagerFactory connectionManagerFactory;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        try {
            when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        } catch (PersistenceException e) {
            throw new RuntimeException("Unexpected exception while performing setUp", e);
        }
        when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);
        topicService = new TopicService(daoFactory, connectionManagerFactory);
    }


    @DataProvider(name = "CreateTopicInvalidParametersProvider")
    public Object[][] provideInvalidCreateMessageMethodParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccountType.USER, 0, false, "salt", null);
        return new Object[][] {
                {null, "title", "text"},
                {account, "title", ""},
                {account, "title", null},
                {account, null, "text"},
                {account, "", "text"},
        };
    }

    @Test
    public void createTopic_ValidParameters_true() throws PersistenceException, ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(true);

        boolean result = topicService.createTopic(account, "title", "text");

        verify(topicDao).createWithGeneratedDate(anyObject());
        Assert.assertTrue(result);
    }

    @Test(dataProvider = "CreateTopicInvalidParametersProvider")
    public void createTopic_InvalidParameters_false(Account account, String title, String text) throws ServiceException {
        boolean result = topicService.createTopic(account, title, text);

        Assert.assertFalse(result);
    }

    @Test
    public void createTopic_couldNotCreateMessageInDatabase_false() throws PersistenceException, ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(false);

        boolean result = topicService.createTopic(account, "title","text");

        Assert.assertFalse(result);
        verify(topicDao).createWithGeneratedDate(anyObject());
    }

    @Test
    public void createTopic_persistenceExceptionThrown_serviceException() throws PersistenceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(topicDao.createWithGeneratedDate(anyObject())).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.createTopic(account, "title","text");
        });
        verify(topicDao).createWithGeneratedDate(anyObject());
    }

    @Test
    public void changeTopicHiddenState_validParameters_true() throws ServiceException, PersistenceException {
        long topicId = 1;
        Topic topic = new Topic(topicId, "title", "text",
                LocalDateTime.of(2013, 3, 5, 1, 12),
                new Account(1),
                false,
                false);
        when(topicDao.findEntityById(topicId)).thenReturn(topic);
        when(topicDao.update(topic)).thenReturn(1);

        boolean result = topicService.changeTopicHiddenState(topicId, true);

        verify(topicDao).update(topic);
        Assert.assertTrue(result);
    }

    @Test
    public void changeTopicHiddenState_topicNotFoundInDatabase_false() throws ServiceException, PersistenceException {
        long topicId = 1;
        when(topicDao.findEntityById(topicId)).thenReturn(null);

        boolean result = topicService.changeTopicHiddenState(1,true);

        verify(topicDao).findEntityById(topicId);
        Assert.assertFalse(result);
    }

    @Test
    public void closeTopic_ValidParameters_true() throws ServiceException, PersistenceException {
        long topicId = 1;
        Topic topic = new Topic(1, "title", "text",
                LocalDateTime.of(2013, 3, 5, 1, 12),
                new Account(1),
                false,
                false);
        when(topicDao.findEntityById(topicId)).thenReturn(topic);
        when(topicDao.update(topic)).thenReturn(1);
        MessageDao messageDao = mock(MessageDao.class);
        when(daoFactory.createMessageDao(connectionManager)).thenReturn(messageDao);
        when(messageDao.findMessagesByTopicId(1)).thenReturn(new ArrayList<Message>() {
            {
                add(new Message(5, "text", new Account(3),
                        LocalDateTime.of(2001, 10, 10, 10, 10),
                        topic));
                add(new Message(7, "text1", new Account(2),
                        LocalDateTime.of(2001, 10, 10, 10, 10),
                        topic));
                add(new Message(15, "text2", new Account(2),
                        LocalDateTime.of(2001, 10, 10, 10, 10),
                        topic));
            }
        });
        AccountDao accountDao = mock(AccountDao.class);
        Account account1 = new Account(2);
        account1.setAccountType(AccountType.VOLUNTEER);
        Account account2 = new Account(3);
        account2.setAccountType(AccountType.ADMIN);
        when(accountDao.findEntityById(2)).thenReturn(account1);
        when(accountDao.findEntityById(3)).thenReturn(account2);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        boolean result = topicService.closeTopic(1);

        verify(accountDao).update(account1);
        verify(accountDao).update(account2);
        verify(topicDao).update(topic);
        Assert.assertTrue(result);
    }

    @Test
    public void closeTopic_topicNotFoundInDatabase_false() throws ServiceException, PersistenceException {
        long topicId = 1;
        when(topicDao.findEntityById(topicId)).thenReturn(null);

        boolean result = topicService.closeTopic(topicId);

        verify(topicDao).findEntityById(topicId);
        Assert.assertFalse(result);
    }

    @Test
    public void findTopicById_topicExists_topicFound() throws ServiceException, PersistenceException {
        long topicId = 1;
        long accountId = 1;
        Topic expected = new Topic(topicId, "title", "text",
                LocalDateTime.of(2013, 3, 5, 1, 12),
                new Account(accountId),
                false,
                false);
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(accountId)).thenReturn(new Account(accountId));
        when(topicDao.findEntityById(topicId)).thenReturn(expected);

        Topic result = topicService.findTopicById(topicId);

        verify(topicDao).findEntityById(accountId);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void findTopicById_PersistenceExceptionWhileFindingTopic_serviceException() throws PersistenceException {
        long topicId = 1;
        when(topicDao.findEntityById(topicId)).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicById(topicId);
        });
        verify(topicDao).findEntityById(topicId);
    }

    @Test
    public void findTopicById_persistenceExceptionWhileCreatingConnectionManager_ServiceException() throws PersistenceException {
        long topicId = 1;
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicById(topicId);
        });
    }

    @Test
    public void findAllTopics_noPersistenceExceptionThrown_threeTopics() throws PersistenceException, ServiceException {
        Account account1 = new Account(1);
        Account account2 = new Account(2);
        when(topicDao.findAll())
                .thenReturn(createTwoTopicsUsingFirstAccountAndThirdUsingSecondAccount(account1, account2));
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(account1.getAccountId())).thenReturn(new Account(1));
        when(accountDao.findEntityById(account2.getAccountId())).thenReturn(new Account(2));

        List<Topic> actual = topicService.findAllTopics();

        verify(topicDao).findAll();
        Assert.assertEquals(actual.size(), 3);
    }

    @Test
    public void findAllTopics_persistenceExceptionThrownInTopicDao_ServiceException() throws PersistenceException {
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(topicDao.findAll()).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findAllTopics();
        });
        verify(daoFactory).createAccountDao(connectionManager);
        verify(topicDao).findAll();
    }

    @Test
    public void findAllTopics_persistenceExceptionThrownInAccountDao_ServiceException() throws PersistenceException {
        Account account1 = new Account(1);
        Topic expected1 = new Topic(1, "title1", "text2",
                LocalDateTime.of(2113, 10, 3, 1, 10),
                account1,
                false,
                false);
        Account account2 = new Account(2);
        Topic expected2 = new Topic(3, "title4", "text4",
                LocalDateTime.of(2012, 4, 7, 4, 38),
                account2,
                true,
                true);
        when(topicDao.findAll()).thenReturn(new ArrayList<Topic>() {
            {
                add(expected1);
                add(expected2);
            }
        });
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(account1.getAccountId())).thenReturn(new Account(1));
        when(accountDao.findEntityById(account2.getAccountId())).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findAllTopics();
        });
    }

    @Test
    public void findAllTopics_disableAutoCommitPersistenceException_serviceException() throws PersistenceException {
        doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findAllTopics();
        });
    }

    @DataProvider(name = "SearchStringProvider")
    public Object[][] provideSearchStrings() {
        return new Object[][] {
                {"title", 3},
                {"2", 1},
                {"title1", 1},
                {"abc", 0}
        };
    }

    @Test(dataProvider = "SearchStringProvider")
    public void findTopicsByTitleSubstringRegex_SessionAccountTypeAdmin_topicsFound(String searchString, int expectedSize)
            throws PersistenceException, ServiceException {
        Account sessionAccount = new Account(15);
        sessionAccount.setAccountType(AccountType.ADMIN);
        Account account1 = new Account(1);
        Account account2 = new Account(2);
        when(topicDao.findAll())
                .thenReturn(createTwoTopicsUsingFirstAccountAndThirdUsingSecondAccount(account1, account2));
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(account1.getAccountId())).thenReturn(new Account(1));
        when(accountDao.findEntityById(account2.getAccountId())).thenReturn(new Account(2));

        List<Topic> actual = topicService.findTopicsByTitleSubstring(sessionAccount, searchString);

        verify(topicDao).findAll();
        Assert.assertEquals(actual.size(), expectedSize);
    }

    @Test(dataProvider = "SearchStringProvider")
    public void findTopicsByTitleSubstringRegex_SessionAccountTypeUser_topicsFound(String searchString, int expectedSize)
            throws PersistenceException, ServiceException {
        Account sessionAccount = new Account(1);
        sessionAccount.setAccountType(AccountType.USER);
        Account account = new Account(1);
        when(topicDao.findTopicsByAccountId(sessionAccount.getAccountId())).thenReturn(createTopics(account));
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(account.getAccountId())).thenReturn(new Account(1));

        List<Topic> actual = topicService.findTopicsByTitleSubstring(sessionAccount, searchString);

        verify(topicDao).findTopicsByAccountId(sessionAccount.getAccountId());
        Assert.assertEquals(actual.size(), expectedSize);
    }

    @Test
    public void findTopicsByTitleSubstring_substringNullAccountTypeAdmin_noTopicsFound() throws ServiceException {
        Account sessionAccount = new Account(15);
        sessionAccount.setAccountType(AccountType.ADMIN);
        int expectedSize = 0;

        List<Topic> actual = topicService.findTopicsByTitleSubstring(sessionAccount,null);

        Assert.assertEquals(expectedSize, actual.size());
    }

    @Test
    public void findTopicsByTitleSubstring_sessionAccountNull_noTopicsFound() throws PersistenceException, ServiceException {
        int expectedSize = 0;
        String searchString = "title";
        Account account1 = new Account(1);
        Account account2 = new Account(2);
        when(topicDao.findAll())
                .thenReturn(createTwoTopicsUsingFirstAccountAndThirdUsingSecondAccount(account1, account2));

        List<Topic> actual = topicService.findTopicsByTitleSubstring(null, searchString);

        verify(topicDao, times(0)).findAll();
        Assert.assertEquals(actual.size(), expectedSize);
    }

    @Test
    public void findTopicsByTitleSubstring_sessionAccountTypeGuest_noTopicsFound() throws PersistenceException, ServiceException {
        Account sessionAccount = new Account(1);
        sessionAccount.setAccountType(AccountType.GUEST);
        int expectedSize = 0;
        String searchString = "title";
        Account account1 = new Account(1);
        Account account2 = new Account(2);
        when(topicDao.findAll())
                .thenReturn(createTwoTopicsUsingFirstAccountAndThirdUsingSecondAccount(account1, account2));

        List<Topic> actual = topicService.findTopicsByTitleSubstring(sessionAccount, searchString);

        verify(topicDao, times(0)).findAll();
        Assert.assertEquals(actual.size(), expectedSize);
    }

    @Test
    public void findTopicsByTitleSubstringRegex_persistenceExceptionThrownByTopicDao_ServiceException() throws PersistenceException {
        Account sessionAccount = new Account(15);
        sessionAccount.setAccountType(AccountType.ADMIN);
        when(topicDao.findAll()).thenThrow(new PersistenceException());
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicsByTitleSubstring(sessionAccount,"title");
        });
        verify(daoFactory).createAccountDao(connectionManager);
        verify(topicDao).findAll();
    }

    @Test
    public void findTopicsByTitleSubstringRegex_persistenceExceptionThrownByAccountDao_ServiceException() throws PersistenceException {
        Account sessionAccount = new Account(15);
        sessionAccount.setAccountType(AccountType.ADMIN);
        Account account1 = new Account(1);
        Account account2 = new Account(2);
        when(topicDao.findAll())
                .thenReturn(createTwoTopicsUsingFirstAccountAndThirdUsingSecondAccount(account1, account2));
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(account1.getAccountId())).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicsByTitleSubstring(sessionAccount,"title");
        });
        verify(daoFactory).createAccountDao(connectionManager);
        verify(topicDao).findAll();
        verify(accountDao).findEntityById(account1.getAccountId());
    }

    @Test
    public void findTopicsByAuthorId_noPersistenceException_topicsFound() throws PersistenceException, ServiceException {
        long accountId = 1;
        Account account = new Account(accountId);
        when(topicDao.findTopicsByAccountId(accountId)).thenReturn(createTopics(account));
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(account.getAccountId())).thenReturn(new Account(accountId));

        List<Topic> actual = topicService.findTopicsByAuthorId(accountId);

        verify(topicDao).findTopicsByAccountId(accountId);
        verify(daoFactory).createAccountDao(connectionManager);
        Assert.assertEquals(actual.size(), 3);
    }

    @Test
    public void findTopicsByAuthorId_persistenceExceptionThrownInTopicDao_serviceException() throws PersistenceException {
        long accountId = 1;
        when(topicDao.findTopicsByAccountId(accountId)).thenThrow(new PersistenceException());
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicsByAuthorId(accountId);
        });
        verify(daoFactory).createAccountDao(connectionManager);
        verify(topicDao).findTopicsByAccountId(accountId);
    }

    @Test
    public void findTopicsByAuthorId_persistenceExceptionThrownInAccountDao_serviceException() throws PersistenceException {
        long accountId = 1;
        Account account = new Account(accountId);
        when(topicDao.findTopicsByAccountId(accountId)).thenReturn(createTopics(account));
        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(accountId)).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicsByAuthorId(accountId);
        });
        verify(daoFactory).createAccountDao(connectionManager);
        verify(accountDao).findEntityById(accountId);
        verify(topicDao).findTopicsByAccountId(accountId);
    }

    @Test
    public void findTopicsByAuthorId_persistenceExceptionDisableAutoCommit_serviceException() throws PersistenceException {
        long accountId = 1;
        doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findTopicsByAuthorId(accountId);
        });
    }

    @Test
    public void findPageTopics_validParametersShowHiddenTrue_topicsFound() throws PersistenceException, ServiceException {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 1;
        int currentPage = 1;
        int numberOfTopicsPerPage = 3;
        Account account = new Account(accountId, login, hash,
            email, AccountType.ADMIN, 0, false, "salt", null);
        List<Topic> topicsFromDao = createTopics(account);
        when(topicDao.findPageTopics(currentPage, numberOfTopicsPerPage, true))
                .thenReturn(topicsFromDao);

        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(accountId)).thenReturn(account);

        List<Topic> actual = topicService.findPageTopics(currentPage, numberOfTopicsPerPage, true);

        verify(topicDao).findPageTopics(currentPage, numberOfTopicsPerPage, true);
        verify(daoFactory).createAccountDao(connectionManager);
        verify(accountDao, times(3)).findEntityById(accountId);
        Assert.assertEquals(actual.size(), 3);
    }

    @Test
    public void findPageTopics_validParametersShowHiddenFalse_hiddenTopicsNotFound() throws PersistenceException, ServiceException {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 1;
        int currentPage = 1;
        int numberOfTopicsPerPage = 3;
        Account account = new Account(accountId, login, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);
        boolean showHidden = false;
        List<Topic> topicsFromDao = new ArrayList<>();
        Topic topic1 = new Topic(1, "title1", "text2",
                LocalDateTime.of(2113, 10, 3, 1, 10),
                account,
                false,
                false);
        Topic topic2 = new Topic(2, "title2", "text2",
                LocalDateTime.of(2013, 1, 5, 10, 12),
                account,
                false,
                false);
        topicsFromDao.add(topic1);
        topicsFromDao.add(topic2);
        when(topicDao.findPageTopics(currentPage, numberOfTopicsPerPage, showHidden))
                .thenReturn(topicsFromDao);

        AccountDao accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        when(accountDao.findEntityById(accountId)).thenReturn(account);

        List<Topic> actual = topicService.findPageTopics(currentPage, numberOfTopicsPerPage, showHidden);

        verify(topicDao).findPageTopics(currentPage, numberOfTopicsPerPage, showHidden);
        verify(daoFactory).createAccountDao(connectionManager);
        verify(accountDao, times(2)).findEntityById(accountId);
        Assert.assertEquals(actual.size(), 2);
    }

    @Test
    public void findPageTopics_persistenceExceptionThrown_serviceException() throws PersistenceException {
        int currentPage = 1;
        int numberOfTopicsPerPage = 2;
        when(topicDao.findPageTopics(currentPage, numberOfTopicsPerPage, true))
                .thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findPageTopics(currentPage, numberOfTopicsPerPage, true);
        });
        verify(topicDao).findPageTopics(currentPage, numberOfTopicsPerPage, true);
    }

    @Test
    public void findPageTopics_persistenceExceptionThrownDisableAutoCommit_serviceException() throws PersistenceException {
        int currentPage = 1;
        int numberOfTopicsPerPage = 3;
        doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

        Assert.assertThrows(ServiceException.class, () -> {
            topicService.findPageTopics(currentPage, numberOfTopicsPerPage, true);
        });
        verify(connectionManager).disableAutoCommit();
    }

    private List<Topic> createTopics(Account account) {
        Topic expected1 = new Topic(1, "title1", "text2",
                LocalDateTime.of(2113, 10, 3, 1, 10),
                account,
                false,
                false);
        Topic expected2 = new Topic(2, "title2", "text2",
                LocalDateTime.of(2013, 1, 5, 10, 12),
                account,
                false,
                false);
        Topic expected3 = new Topic(3, "title4", "text4",
                LocalDateTime.of(2012, 4, 7, 4, 38),
                account,
                true,
                true);
        return new ArrayList<Topic>() {
            {
                add(expected1);
                add(expected2);
                add(expected3);
            }
        };
    }

    private List<Topic> createTwoTopicsUsingFirstAccountAndThirdUsingSecondAccount(Account account1, Account account2) {
        Topic expected1 = new Topic(1, "title1", "text2",
                LocalDateTime.of(2113, 10, 3, 1, 10),
                account1,
                false,
                false);
        Topic expected2 = new Topic(2, "title2", "text2",
                LocalDateTime.of(2013, 1, 5, 10, 12),
                account1,
                false,
                false);
        Topic expected3 = new Topic(3, "title3", "text3",
                LocalDateTime.of(2012, 4, 7, 4, 38),
                account2,
                true,
                true);
        return new ArrayList<Topic>() {
            {
                add(expected1);
                add(expected2);
                add(expected3);
            }
        };
    }
}
