package com.epam.finaltask.service;

import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Message;
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

public class MessageServiceTest {

    MessageService messageService;
    MessageDao messageDao;
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

        messageDao = mock(MessageDao.class);
        when(daoFactory.createMessageDao(connectionManager)).thenReturn(messageDao);

        messageService = new MessageService(daoFactory, connectionManagerFactory);
    }

    @DataProvider(name = "CreateMessageInvalidParametersProvider")
    public Object[][] provideInvalidCreateMessageMethodParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        StringBuilder tooLongStringBuilder = new StringBuilder();
        for (int i = 0; i < 257; i++) {
            tooLongStringBuilder.append("a");
        }
        return new Object[][] {
                {null, 1, "text"},
                {account, 1, ""},
                {account, 1, null},
                {account, 1, tooLongStringBuilder.toString()},
        };
    }

    @Test
    public void createMessageTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(true);

            boolean result = messageService.createMessage(account, 1, "text");

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(dataProvider = "CreateMessageInvalidParametersProvider")
    public void createMessageTestInvalidParameters(Account account, long topicId, String text) {
        try {
            when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(true);

            boolean result = messageService.createMessage(account, topicId, text);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createMessageTestCouldNotCreateMessageInDatabase() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(false);

            boolean result = messageService.createMessage(account, 1, "text");

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void deleteMessageTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        try {
            when(messageDao.findEntityById(1))
                    .thenReturn(new Message(1,
                            "message",
                            new Account(1),
                            LocalDateTime.of(2000, 1, 3, 4, 5),
                            new Topic(1)));

            when(messageDao.delete(1)).thenReturn(true);

            boolean result = messageService.deleteMessage(account, 1);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void deleteMessageTestAccountNotAdmin() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(messageDao.findEntityById(1))
                    .thenReturn(new Message(1,
                            "message",
                            new Account(1),
                            LocalDateTime.of(2000, 1, 3, 4, 5),
                            new Topic(1)));

            when(messageDao.delete(1)).thenReturn(true);

            boolean result = messageService.deleteMessage(account, 1);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }
}
