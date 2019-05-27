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
import static org.testng.Assert.fail;

public class MessageDaoImplTest {

    private static final String BEFORE_METHOD_MESSAGE_TEXT = "text";
    private MessageDaoImpl messageDao;
    private Message message;
    @Mock
    private AbstractConnectionManager connectionManager;

    @BeforeClass
    public void init() {
        try {
            MockitoAnnotations.initMocks(this);
            DatabaseTestUtil.registerDrivers();
            DatabaseTestUtil.initializeDatabase();
            Connection connection = DatabaseTestUtil.getConnection();
            when(connectionManager.getConnection()).thenReturn(connection);
            messageDao = new MessageDaoImpl(connectionManager);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeMethod
    public void initBeforeMethod() {
        message = new Message(BEFORE_METHOD_MESSAGE_TEXT,
                new Account(1),
                LocalDateTime.of(2000, 2, 15, 21, 54),
                new Topic(1));
    }

    @AfterMethod
    public void cleanUpDatabaseMessages() {
        try {
            for (Message databaseMessage : messageDao.findAll()) {
                messageDao.delete(databaseMessage.getMessageId());
            }
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findPageMessagesTest() {
        try {
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
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createWithGeneratedDateTest() {
        try {
            messageDao.createWithGeneratedDate(message);
            message.setDate(null);
            Message actual = messageDao.findMessagesByTopicId(1).get(0);
            Assert.assertNotNull(actual.getDate());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findMessagesByTopicIdTest() {
        try {
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
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAllTest() {
        try {
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
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findEntityByIdTest() {
        try {
            messageDao.create(message);
            Message messageByTopicId = messageDao.findMessagesByTopicId(1).get(0);

            Message actual = messageDao.findEntityById(messageByTopicId.getMessageId());

            Assert.assertEquals(actual.getText(), messageByTopicId.getText());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createTest() {
        try {
            boolean result = messageDao.create(message);

            Assert.assertTrue(result);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void updateTest() {
        try {
            messageDao.create(message);
            Message messageToUpdate = messageDao.findMessagesByTopicId(1).get(0);
            messageToUpdate.setText("changed");
            System.out.println(messageToUpdate);
            int actual = messageDao.update(messageToUpdate);

            Assert.assertEquals(actual, 1);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void countMessagesByTopicIdTest() {
        try {
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
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        try {
            DatabaseTestUtil.dropSchema();
            DatabaseTestUtil.deregisterDrivers();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
