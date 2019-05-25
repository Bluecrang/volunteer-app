package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.TopicDao;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Account;
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
import static org.testng.Assert.fail;

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
                email, AccountType.USER, 0, true, false, "salt", null);
        return new Object[][] {
                {null, "title", "text"},
                {account, "title", ""},
                {account, "title", null},
                {account, null, "text"},
                {account, "", "text"},
        };
    }

    @Test
    public void createTopicTestValidParameters() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(true);

            boolean result = topicService.createTopic(account, "title", "text");

            verify(topicDao).createWithGeneratedDate(anyObject());
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(dataProvider = "CreateTopicInvalidParametersProvider")
    public void createTopicTestInvalidParameters(Account account, String title, String text) {
        try {

            boolean result = topicService.createTopic(account, title, text);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void createTopicTestCouldNotCreateMessageInDatabase() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(false);

            boolean result = topicService.createTopic(account, "title","text");

            verify(topicDao).createWithGeneratedDate(anyObject());
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void createTopicTestPersistenceExceptionThrown() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.createWithGeneratedDate(anyObject())).thenThrow(new PersistenceException());

            topicService.createTopic(account, "title","text");
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeTopicHiddenStateTestValidParameters() {
        long topicId = 1;
        Topic topic = new Topic(topicId, "title", "text",
                LocalDateTime.of(2013, 3, 5, 1, 12),
                new Account(1),
                false,
                false);
        try {
            when(topicDao.findEntityById(topicId)).thenReturn(topic);
            when(topicDao.update(topic)).thenReturn(1);
            boolean result = topicService.changeTopicHiddenState(topicId, true);

            verify(topicDao).update(topic);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeTopicHiddenStateTestTopicNotFoundInDatabase() {
        long topicId = 1;
        try {
            when(topicDao.findEntityById(topicId)).thenReturn(null);
            boolean result = topicService.changeTopicHiddenState(1,true);

            verify(topicDao).findEntityById(topicId);
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void closeTopicTestValidParameters() {
        long topicId = 1;
        Topic topic = new Topic(1, "title", "text",
                LocalDateTime.of(2013, 3, 5, 1, 12),
                new Account(1),
                false,
                false);
        try {
            when(topicDao.findEntityById(topicId)).thenReturn(topic);
            when(topicDao.update(topic)).thenReturn(1);
            boolean result = topicService.closeTopic(1);

            verify(topicDao).update(topic);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void closeTopicTestTopicNotFoundInDatabase() {
        long topicId = 1;
        try {
            when(topicDao.findEntityById(topicId)).thenReturn(null);
            boolean result = topicService.closeTopic(topicId);

            verify(topicDao).findEntityById(topicId);
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findTopicByIdTestTopicExists() {
        long topicId = 1;
        long accountId = 1;
        try {
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
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findTopicByIdTestPersistenceExceptionWhileFindingTopic() throws ServiceException {
        long topicId = 1;
        try {
            when(topicDao.findEntityById(topicId)).thenThrow(new PersistenceException());
            topicService.findTopicById(topicId);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findTopicByIdTestPersistenceExceptionWhileCreatingConnectionManager() throws ServiceException {
        long topicId = 1;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
            topicService.findTopicById(topicId);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAllTopicsTestNoPersistenceException() {
        try {
            Account account1 = new Account(1);
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
            Account account2 = new Account(2);
            Topic expected3 = new Topic(3, "title4", "text4",
                    LocalDateTime.of(2012, 4, 7, 4, 38),
                    account2,
                    true,
                    true);
            when(topicDao.findAll()).thenReturn(new ArrayList<Topic>() {
                {
                    add(expected1);
                    add(expected2);
                    add(expected3);
                }
            });
            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
            when(accountDao.findEntityById(account1.getAccountId())).thenReturn(new Account(1));
            when(accountDao.findEntityById(account2.getAccountId())).thenReturn(new Account(2));

            List<Topic> actual = topicService.findAllTopics();

            verify(topicDao).findAll();
            Assert.assertEquals(actual.size(), 3);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findAllTopicsTestPersistenceExceptionThrownInTopicDao() throws ServiceException {
        try {
            when(topicDao.findAll()).thenThrow(new PersistenceException());
            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

            topicService.findAllTopics();

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findAllTopicsTestPersistenceExceptionThrownInAccountDao() throws ServiceException {
        try {
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

            topicService.findAllTopics();

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findAllTopicsTestPersistenceExceptionBeforeTransaction() throws ServiceException {
        try {
            doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();
            topicService.findAllTopics();

            verify(connectionManager).disableAutoCommit();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
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
    public void findTopicsByTitleSubstringRegexTestNoPersistenceException(String searchString, int expectedSize) {
        try {
            Account account1 = new Account(1);
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
            Account account2 = new Account(2);
            Topic expected3 = new Topic(3, "title3", "text3",
                    LocalDateTime.of(2012, 4, 7, 4, 38),
                    account2,
                    true,
                    true);
            when(topicDao.findAll()).thenReturn(new ArrayList<Topic>() {
                {
                    add(expected1);
                    add(expected2);
                    add(expected3);
                }
            });
            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
            when(accountDao.findEntityById(account1.getAccountId())).thenReturn(new Account(1));
            when(accountDao.findEntityById(account2.getAccountId())).thenReturn(new Account(2));

            List<Topic> actual = topicService.findTopicsByTitleSubstring(searchString);

            verify(topicDao).findAll();
            Assert.assertEquals(actual.size(), expectedSize);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void findTopicsByTitleSubstringTestSubstringNull() {
        int expectedSize = 0;
        try {
            List<Topic> actual = topicService.findTopicsByTitleSubstring(null);

            Assert.assertEquals(actual.size(), expectedSize);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findTopicsByTitleSubstringRegexTestPersistenceExceptionThrownByTopicDao() throws ServiceException {
        try {
            when(topicDao.findAll()).thenThrow(new PersistenceException());
            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

            topicService.findTopicsByTitleSubstring("title");
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findTopicsByTitleSubstringRegexTestPersistenceExceptionThrownByAccountDao() throws ServiceException {
        try {
            Account account1 = new Account(1);
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
            Account account2 = new Account(2);
            Topic expected3 = new Topic(3, "title3", "text3",
                    LocalDateTime.of(2012, 4, 7, 4, 38),
                    account2,
                    true,
                    true);
            when(topicDao.findAll()).thenReturn(new ArrayList<Topic>() {
                {
                    add(expected1);
                    add(expected2);
                    add(expected3);
                }
            });
            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
            when(accountDao.findEntityById(account1.getAccountId())).thenThrow(new PersistenceException());

            topicService.findTopicsByTitleSubstring("title");
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }
}
