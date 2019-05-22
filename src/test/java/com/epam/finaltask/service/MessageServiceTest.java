package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Account;
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
import static org.testng.Assert.fail;

public class MessageServiceTest {

    private MessageService messageService;
    @Mock
    private MessageDao messageDao;
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
        when(daoFactory.createMessageDao(connectionManager)).thenReturn(messageDao);

        messageService = new MessageService(daoFactory, connectionManagerFactory);
    }

    @DataProvider(name = "CreateMessageInvalidParametersProvider")
    public Object[][] provideInvalidCreateMessageMethodParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
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
                email, AccountType.USER, 0, true, false, "salt", null);
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
                email, AccountType.USER, 0, true, false, "salt", null);
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

    @Test(expectedExceptions = ServiceException.class)
    public void createMessageTestPersistenceExceptionThrown() throws ServiceException {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        long topicId = 1;
        String text = "text";
        Account account = new Account(1, login, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
            messageService.createMessage(account, topicId, text);

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void deleteMessageTestValidParameters() {
        long messageId = 1;
        try {
            when(messageDao.findEntityById(messageId))
                    .thenReturn(new Message(messageId,
                            "message",
                            new Account(1),
                            LocalDateTime.of(2000, 1, 3, 4, 5),
                            new Topic(1)));

            when(messageDao.delete(messageId)).thenReturn(true);

            boolean result = messageService.deleteMessage(messageId);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void deleteMessageTestMessageNotFound() {
        try {
            when(messageDao.findEntityById(1)).thenReturn(null);

            boolean result = messageService.deleteMessage(1);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void deleteMessageTestPersistenceExceptionThrownInTransaction() throws ServiceException {
        long messageId = 1;
        try {
            when(messageDao.findEntityById(messageId)).thenThrow(new PersistenceException());

            messageService.deleteMessage(messageId);

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void deleteMessageTestPersistenceExceptionThrownBeforeTransaction() throws ServiceException {
        long messageId = 1;
        try {
            doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

            messageService.deleteMessage(messageId);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findTopicPageMessagesTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 1;
        long topicId = 1;
        int currentPage = 1;
        int numberOfMessagesPerPage = 3;
        Account account = new Account(accountId, login, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            List<Message> messagesFromDao = new ArrayList<>();
            messagesFromDao.add(new Message(1,
                    "message",
                    new Account(accountId),
                    LocalDateTime.of(2001, 1, 3, 4, 5),
                    new Topic(topicId)));
            messagesFromDao.add(new Message(2,
                    "message",
                    new Account(accountId),
                    LocalDateTime.of(2002, 4, 3, 4, 5),
                    new Topic(topicId)));
            messagesFromDao.add(new Message(3,
                    "message",
                    new Account(accountId),
                    LocalDateTime.of(2000, 1, 2, 6, 1),
                    new Topic(topicId)));
            when(messageDao.findPageMessages(topicId, currentPage, numberOfMessagesPerPage))
                    .thenReturn(messagesFromDao);

            AccountDao accountDao = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
            when(accountDao.findEntityById(accountId)).thenReturn(account);
            List<Message> messageList = messageService.findTopicPageMessages(topicId, currentPage, numberOfMessagesPerPage);

            Assert.assertEquals(messageList.size(), 3);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findTopicPageMessagesTestPersistenceExceptionThrown() throws ServiceException {
        long topicId = 1;
        int currentPage = 1;
        int numberOfMessagesPerPage = 3;
        try {
            when(messageDao.findPageMessages(topicId, currentPage, numberOfMessagesPerPage))
                    .thenThrow(new PersistenceException());
            messageService.findTopicPageMessages(topicId, currentPage, numberOfMessagesPerPage);

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findTopicPageMessagesTestPersistenceExceptionThrownBeforeTransaction() throws ServiceException {
        long topicId = 1;
        int currentPage = 1;
        int numberOfMessagesPerPage = 3;
        try {
            doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

            messageService.findTopicPageMessages(topicId, currentPage, numberOfMessagesPerPage);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void countMessagesTestValidId() {
        long topicId = 1;
        int messageCount = 5;
        try {
            when(messageDao.countMessagesByTopicId(topicId)).thenReturn(messageCount);

            messageService.countMessages(topicId);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void countMessagesTestPersistenceExceptionThrown() throws ServiceException {
        long topicId = 1;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

            messageService.countMessages(topicId);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findMessageByIdTestValidId() {
        long id = 1;
        try {
            when(messageDao.findEntityById(id)).thenReturn(new Message(id,
                    "message",
                    new Account(1),
                    LocalDateTime.of(2000, 1, 3, 4, 5),
                    new Topic(1)));

            messageService. findMessageById(id);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findMessageByIdTestPersistenceExceptionThrown() throws ServiceException {
        long id = 1;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

            messageService.findMessageById(id);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }
}
