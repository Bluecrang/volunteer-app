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
    public void setUp() throws PersistenceException {
        MockitoAnnotations.initMocks(this);
        when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        when(daoFactory.createMessageDao(connectionManager)).thenReturn(messageDao);

        messageService = new MessageService(daoFactory, connectionManagerFactory);
    }

    @Test
    public void createMessage_ValidParameters_messageCreated() throws PersistenceException, ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(true);
        TopicDao topicDao = mock(TopicDao.class);
        Topic topic = new Topic(15);
        topic.setClosed(false);
        when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);
        when(topicDao.findEntityById(anyLong())).thenReturn(topic);

        boolean result = messageService.createMessage(account, 1, "text");

        Assert.assertTrue(result);
        verify(daoFactory).createTopicDao(connectionManager);
        verify(messageDao).createWithGeneratedDate(anyObject());
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
    public void createMessage_InvalidParameters_false(Account account, long topicId, String text) throws ServiceException {
        boolean result = messageService.createMessage(account, topicId, text);

        Assert.assertFalse(result);
    }

    @Test
    public void createMessage_topicClosed_false() throws PersistenceException, ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(true);
        TopicDao topicDao = mock(TopicDao.class);
        Topic topic = new Topic(15);
        topic.setClosed(true);
        when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);
        when(topicDao.findEntityById(anyLong())).thenReturn(topic);

        boolean result = messageService.createMessage(account, 1, "text");

        Assert.assertFalse(result);
        verify(daoFactory).createTopicDao(connectionManager);
        verify(topicDao).findEntityById(anyLong());
    }

    @Test
    public void createMessage_couldNotCreateMessageInDatabase_false() throws PersistenceException, ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(messageDao.createWithGeneratedDate(anyObject())).thenReturn(false);
        TopicDao topicDao = mock(TopicDao.class);
        Topic topic = new Topic(15);
        topic.setClosed(false);
        when(topicDao.findEntityById(anyLong())).thenReturn(topic);
        when(daoFactory.createTopicDao(connectionManager)).thenReturn(topicDao);

        boolean result = messageService.createMessage(account, 1, "text");

        Assert.assertFalse(result);
        verify(messageDao).createWithGeneratedDate(anyObject());
    }

    @Test
    public void createMessage_PersistenceExceptionThrown_ServiceException() throws PersistenceException {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        long topicId = 1;
        String text = "text";
        Account account = new Account(1, login, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.createMessage(account, topicId, text);
        });
        verify(connectionManagerFactory).createConnectionManager();
    }

    @Test
    public void deleteMessage_messageExists_true() throws PersistenceException, ServiceException {
        long messageId = 1;
        when(messageDao.findEntityById(messageId))
                .thenReturn(new Message(messageId,
                        "message",
                        new Account(1),
                        LocalDateTime.of(2000, 1, 3, 4, 5),
                        new Topic(1)));

        when(messageDao.delete(messageId)).thenReturn(true);

        boolean result = messageService.deleteMessage(messageId);

        Assert.assertTrue(result);
        verify(messageDao).findEntityById(messageId);
        verify(messageDao).delete(messageId);
    }

    @Test
    public void deleteMessage_testMessageNotFound_false() throws PersistenceException, ServiceException {
        long messageId = 1;
        when(messageDao.findEntityById(messageId)).thenReturn(null);

        boolean result = messageService.deleteMessage(messageId);

        Assert.assertFalse(result);
        verify(messageDao).findEntityById(messageId);
    }

    @Test
    public void deleteMessage_persistenceExceptionThrownInTransaction_serviceException() throws PersistenceException {
        long messageId = 1;
        when(messageDao.findEntityById(messageId)).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.deleteMessage(messageId);
        });
        verify(messageDao).findEntityById(messageId);
    }

    @Test
    public void deleteMessage_persistenceExceptionThrownBeforeTransaction_serviceException() throws PersistenceException {
        long messageId = 1;
        doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.deleteMessage(messageId);
        });
        verify(messageDao, never()).findEntityById(messageId);
    }

    @Test
    public void findTopicPageMessages_validParameters_messagesFound() throws PersistenceException, ServiceException {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 1;
        long topicId = 1;
        int currentPage = 1;
        int numberOfMessagesPerPage = 3;
        Account account = new Account(accountId, login, hash,
                email, AccountType.USER, 0, false, "salt", null);
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

        List<Message> actual = messageService.findTopicPageMessages(topicId, currentPage, numberOfMessagesPerPage);

        Assert.assertEquals(actual.size(), 3);
        verify(daoFactory).createAccountDao(connectionManager);
        verify(accountDao, times(3)).findEntityById(accountId);
        verify(messageDao).findPageMessages(topicId, currentPage, numberOfMessagesPerPage);
    }

    @Test
    public void findTopicPageMessages_PersistenceExceptionThrown_serviceException() throws PersistenceException {
        long topicId = 1;
        int currentPage = 1;
        int numberOfMessagesPerPage = 3;
        when(messageDao.findPageMessages(topicId, currentPage, numberOfMessagesPerPage))
                .thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.findTopicPageMessages(topicId, currentPage, numberOfMessagesPerPage);
        });
        verify(messageDao).findPageMessages(topicId, currentPage, numberOfMessagesPerPage);
    }

    @Test
    public void findTopicPageMessages_persistenceExceptionThrownDisableAutoCommit_serviceException() throws PersistenceException {
        long topicId = 1;
        int currentPage = 1;
        int numberOfMessagesPerPage = 3;
        doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.findTopicPageMessages(topicId, currentPage, numberOfMessagesPerPage);
        });
        verify(connectionManager).disableAutoCommit();
    }

    @Test
    public void countMessages_validId_expectedMessageCount() throws PersistenceException, ServiceException {
        long topicId = 1;
        int messageCount = 5;
        when(messageDao.countMessagesByTopicId(topicId)).thenReturn(messageCount);

        int result = messageService.countMessages(topicId);

        Assert.assertEquals(result, messageCount);
        verify(messageDao).countMessagesByTopicId(topicId);
    }

    @Test
    public void countMessagesTestPersistenceExceptionThrown() throws PersistenceException {
        long topicId = 1;
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.countMessages(topicId);
        });
        verify(connectionManagerFactory).createConnectionManager();
    }

    @Test
    public void findMessageById_validId_messageFound() throws PersistenceException, ServiceException {
        long id = 1;
        when(messageDao.findEntityById(id)).thenReturn(new Message(id,
                "message",
                new Account(id),
                LocalDateTime.of(2000, 1, 3, 4, 5),
                new Topic(1)));

        Message actual = messageService.findMessageById(id);

        Assert.assertEquals(actual.getMessageId(), id);
        verify(messageDao).findEntityById(id);
    }

    @Test
    public void findMessageByIdTestPersistenceExceptionThrown() throws PersistenceException {
        long id = 1;
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        Assert.assertThrows(ServiceException.class, () -> {
            messageService.findMessageById(id);
        });
        verify(connectionManagerFactory).createConnectionManager();
    }
}
