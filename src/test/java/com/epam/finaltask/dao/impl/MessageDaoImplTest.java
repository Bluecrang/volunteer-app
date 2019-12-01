package com.epam.finaltask.dao.impl;

import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.fail;

public class MessageDaoImplTest {

    private static final String BEFORE_METHOD_MESSAGE_TEXT = "text";
    private MessageDaoImpl messageDao;
    private Message message;
    @Mock
    private AbstractConnectionManager connectionManager;

    @BeforeClass
    public void init() throws SQLException {
        DatabaseTestUtil.registerDrivers();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws SQLException {
        DatabaseTestUtil.deregisterDrivers();
    }

    @BeforeMethod
    public void initBeforeMethod() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
        DatabaseTestUtil.initializeDatabase();
        Connection connection = DatabaseTestUtil.getConnection();
        when(connectionManager.getConnection()).thenReturn(connection);
        messageDao = new MessageDaoImpl(connectionManager);
        message = new Message(BEFORE_METHOD_MESSAGE_TEXT,
                new Account(1),
                LocalDateTime.of(2000, 2, 15, 21, 54),
                new Topic(1));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpDatabaseMessages() throws SQLException, IOException {
        DatabaseTestUtil.dropSchema();
    }

    @Test
    public void findPageMessages_TwoMessagesPerPageThreeMessagesExist_twoMessages() throws PersistenceException {
        messageDao.create(message);
        LocalDateTime localDateTime = LocalDateTime.of(2001, 5, 17, 5, 52);
        messageDao.create(new Message("text1",
                new Account(1),
                localDateTime,
                new Topic(1)));
        messageDao.create(new Message("text2",
                new Account(1),
                LocalDateTime.of(2021, 1, 12, 2, 12),
                new Topic(1)));

        List<Message> actual = messageDao.findPageMessages(1, 1, 2);

        Assert.assertEquals(actual.get(1).getDate(), localDateTime);
    }

    @Test
    public void createWithGeneratedDate_validMessage_true() throws PersistenceException {
        boolean actual = messageDao.createWithGeneratedDate(message);

        Assert.assertTrue(actual);
    }

    @Test
    public void findMessagesByTopicId_threeMessagesExist_threeMessages() throws PersistenceException {
        messageDao.create(message);
        messageDao.create(new Message("text1",
                new Account(1),
                LocalDateTime.of(2001, 5, 17, 5, 52),
                new Topic(1)));
        messageDao.create(new Message("text2",
                new Account(1),
                LocalDateTime.of(2021, 1, 12, 2, 12),
                new Topic(1)));

        List<Message> actual = messageDao.findMessagesByTopicId(1);

        Assert.assertEquals(actual.size(), 3);
    }

    @Test
    public void findAll_threeMessagesExist_threeMessages() throws PersistenceException {
        messageDao.create(message);
        messageDao.create(new Message("text1",
                new Account(1),
                LocalDateTime.of(2001, 5, 17, 5, 52),
                new Topic(1)));
        messageDao.create(new Message("text2",
                new Account(1),
                LocalDateTime.of(2021, 1, 12, 2, 12),
                new Topic(1)));

        List<Message> actual = messageDao.findAll();

        Assert.assertEquals(actual.size(), 3);
    }

    @Test
    public void findEntityById_messageExist_messageFound() throws PersistenceException {
        long messageId = 1;
        messageDao.create(message);

        Message actual = messageDao.findEntityById(messageId);

        Assert.assertEquals(actual.getText(), message.getText());
    }

    @Test
    public void create_validMessage_true() throws PersistenceException {
        boolean result = messageDao.create(message);

        Assert.assertTrue(result);
    }

    @Test
    public void create_topicDoesNotExist_persistenceException() {
        message.setTopic(new Topic(15));

        assertThrows(PersistenceException.class, () -> {
            messageDao.create(message);
        });
    }

    @Test
    public void update_validMessage_success() throws PersistenceException {
        int expected = 1;
        messageDao.create(message);
        message.setMessageId(1);
        message.setText("changed");

        int actual = messageDao.update(message);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void update_messageDoesNotExist_false() throws PersistenceException {
        int expected = 0;
        message.setMessageId(153);

        int actual = messageDao.update(message);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void update_topicDoesNotExist_persistenceException() throws PersistenceException {
        messageDao.create(message);
        message.setTopic(new Topic(2));
        message.setMessageId(1);

        assertThrows(PersistenceException.class, () -> {
            messageDao.update(message);
        });
    }

    @Test
    public void countMessagesByTopicId_topicExistsThreeMessagesExist_three() throws PersistenceException {
        messageDao.create(message);
        messageDao.create(new Message("text1",
                new Account(1),
                LocalDateTime.of(2001, 5, 17, 5, 52),
                new Topic(1)));
        messageDao.create(new Message("text2",
                new Account(1),
                LocalDateTime.of(2021, 1, 12, 2, 12),
                new Topic(1)));

        int actual = messageDao.countMessagesByTopicId(1);

        Assert.assertEquals(actual, 3);
    }
}
