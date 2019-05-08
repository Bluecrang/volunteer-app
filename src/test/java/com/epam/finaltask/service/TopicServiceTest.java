package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.TopicDao;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Topic;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class TopicServiceTest {

    TopicService topicService;
    TopicDao topicDao;
    AbstractConnectionManager connectionManager;
    DaoFactory daoFactory;

    @BeforeMethod
    public void setUp() {

        ConnectionManagerFactory connectionManagerFactory = mock(ConnectionManagerFactory.class);

        connectionManager = mock(AbstractConnectionManager.class);
        try {
            when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        } catch (PersistenceException e) {
            throw new RuntimeException("Unexpected exception while performing setUp", e);
        }

        daoFactory = mock(DaoFactory.class);

        topicDao = mock(TopicDao.class);
        when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);

        topicService = new TopicService(daoFactory, connectionManagerFactory);
    }


    @DataProvider(name = "CreateTopicInvalidParametersProvider")
    public Object[][] provideInvalidCreateMessageMethodParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
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
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(true);

            boolean result = topicService.createTopic(account, "title", "text");

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
            when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(true);

            boolean result = topicService.createTopic(account, title, text);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createTopicTestCouldNotCreateMessageInDatabase() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.createWithGeneratedDate(anyObject())).thenReturn(false);

            boolean result = topicService.createTopic(account, "title","text");

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeTopicHiddenStateTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        try {
            when(topicDao.findEntityById(1)).thenReturn(new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    new Account(1),
                    false,
                    false));
            when(topicDao.update(anyObject())).thenReturn(1);
            boolean result = topicService.changeTopicHiddenState(account, 1,true);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeTopicHiddenStateTestAccountNotAdmin() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.findEntityById(1)).thenReturn(new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    new Account(1),
                    false,
                    false));
            when(topicDao.update(anyObject())).thenReturn(1);
            boolean result = topicService.changeTopicHiddenState(account, 1,true);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeTopicHiddenStateTestAccountNull() {
        Account account = null;
        try {
            when(topicDao.findEntityById(1)).thenReturn(new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    new Account(1),
                    false,
                    false));
            when(topicDao.update(anyObject())).thenReturn(1);
            boolean result = topicService.changeTopicHiddenState(account, 1,true);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeTopicHiddenStateTestTopicNotFoundInDatabase() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        try {
            when(topicDao.findEntityById(1)).thenReturn(null);
            when(topicDao.update(null)).thenReturn(0);
            boolean result = topicService.changeTopicHiddenState(account, 1,true);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void closeTopicTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        try {
            when(topicDao.findEntityById(1)).thenReturn(new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    new Account(1),
                    false,
                    false));
            when(topicDao.update(anyObject())).thenReturn(1);
            boolean result = topicService.closeTopic(account, 1);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void closeTopicTestAccountNotAdmin() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(topicDao.findEntityById(1)).thenReturn(new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    new Account(1),
                    false,
                    false));
            when(topicDao.update(anyObject())).thenReturn(1);
            boolean result = topicService.closeTopic(account, 1);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void closeTopicTestAccountNull() {
        Account account = null;
        try {
            when(topicDao.findEntityById(1)).thenReturn(new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    new Account(1),
                    false,
                    false));
            when(topicDao.update(anyObject())).thenReturn(1);
            boolean result = topicService.closeTopic(account, 1);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void closeTopicTestTopicNotFoundInDatabase() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        try {
            when(topicDao.findEntityById(1)).thenReturn(null);
            when(topicDao.update(null)).thenReturn(0);
            boolean result = topicService.closeTopic(account, 1);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findTopicByTitleTestValidTitle() {
        String title = "title";
        try {
            Account account = new Account(1);
            Topic expected = new Topic(1, "title", "text",
                    LocalDateTime.of(2013, 3, 5, 1, 12),
                    account,
                    false,
                    false);
            when(topicDao.findTopicByTitle(title)).thenReturn(expected);
            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(anyObject())).thenReturn(accountDao);
            when(accountDao.findEntityById(account.getAccountId())).thenReturn(new Account(1));

            Topic actual = topicService.findTopicByTitle(title);

            Assert.assertEquals(actual, expected);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void findTopicByTitleTestTitleNull() {
        String title = null;
        try {
            when(topicDao.findTopicByTitle(title)).thenReturn(null);

            Topic actual = topicService.findTopicByTitle(title);

            Assert.assertNull(actual);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void findTopicByTitleTestTitleBlank() {
        String title = "";
        try {
            when(topicDao.findTopicByTitle(title)).thenReturn(null);

            Topic actual = topicService.findTopicByTitle(title);

            Assert.assertNull(actual);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }
}
