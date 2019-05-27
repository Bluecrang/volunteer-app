package com.epam.finaltask.service;

import com.epam.finaltask.dao.*;
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

    @Test
    public void createMessageTestValidParameters() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        try {
            when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(true);
            TopicDao topicDao = mock(TopicDao.class);
            Topic topic = new Topic(15);
            topic.setClosed(false);
            when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);
            when(topicDao.findEntityById(anyLong())).thenReturn(topic);

            boolean result = messageService.createMessage(account, 1, "text");

            verify(messageDao).createWithGeneratedDate(anyObject());
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @DataProvider(name = "CreateMessageInvalidParametersProvider")
    public Object[][] provideInvalidCreateMessageMethodParameters() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
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

    @Test(dataProvider = "CreateMessageInvalidParametersProvider")
    public void createMessageTestInvalidParameters(Account account, long topicId, String text) {
        try {
            boolean result = messageService.createMessage(account, topicId, text);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void createMessageTestTopicClosed() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        try {
            when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(true);
            TopicDao topicDao = mock(TopicDao.class);
            Topic topic = new Topic(15);
            topic.setClosed(true);
            when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);
            when(topicDao.findEntityById(anyLong())).thenReturn(topic);

            boolean result = messageService.createMessage(account, 1, "text");

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createMessageTestCouldNotCreateMessageInDatabase() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        try {
            when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(false);
            TopicDao topicDao = mock(TopicDao.class);
            Topic topic = new Topic(15);
            topic.setClosed(false);
            when(topicDao.findEntityById(anyLong())).thenReturn(topic);
            when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);

            boolean result = messageService.createMessage(account, 1, "text");

            verify(messageDao).createWithGeneratedDate(anyObject());
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
                email, AccountType.ADMIN, 0, false, "salt", null);
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

            verify(messageDao).findEntityById(messageId);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void deleteMessageTestMessageNotFound() {
        long mesageId = 1;
        try {
            when(messageDao.findEntityById(mesageId)).thenReturn(null);

            boolean result = messageService.deleteMessage(mesageId);

            verify(messageDao).findEntityById(mesageId);
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

            verify(messageDao).findEntityById(messageId);
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
                email, AccountType.USER, 0, false, "salt", null);
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

            verify(messageDao).findPageMessages(topicId, currentPage, numberOfMessagesPerPage);
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

            int result = messageService.countMessages(topicId);

            verify(messageDao).countMessagesByTopicId(topicId);
            Assert.assertEquals(result, messageCount);
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
                    new Account(id),
                    LocalDateTime.of(2000, 1, 3, 4, 5),
                    new Topic(1)));

            Message actual = messageService.findMessageById(id);

            verify(messageDao).findEntityById(id);
            Assert.assertEquals(actual.getMessageId(), id);
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
