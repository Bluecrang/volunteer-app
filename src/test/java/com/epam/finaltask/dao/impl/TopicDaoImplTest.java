package com.epam.finaltask.dao.impl;

import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Topic;
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

public class TopicDaoImplTest {

    private static final String BEFORE_METHOD_TOPIC_TITLE = "bftitle";
    private static final String BEFORE_METHOD_TOPIC_TEXT = "bftext";
    ConnectionManager connectionManager;
    TopicDaoImpl topicDao;
    Connection connection;
    Topic topic;

    @BeforeClass
    public void init() {
        connectionManager = mock(ConnectionManager.class);
        try {
            connection = DatabaseTestUtil.initiateDatabaseAndGetConnection();
            when(connectionManager.getConnection()).thenReturn(connection);
            topicDao = new TopicDaoImpl(connectionManager);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeMethod
    public void initBeforeMethod() {
        topic = new Topic(BEFORE_METHOD_TOPIC_TITLE,
                BEFORE_METHOD_TOPIC_TEXT,
                LocalDateTime.of(2000, 2, 15, 21, 54),
                new Account(1),
                false,
                false);
    }

    @AfterMethod
    public void cleanUpDatabaseTopics() {
        try {
            List<Topic> topics = topicDao.findAll();
            if (topics != null) {
                for (Topic databaseTopic : topics) {
                    if (databaseTopic.getTopicId() != 1) {
                        topicDao.delete(databaseTopic.getTopicId());
                    }
                }
            }
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createWithGeneratedDateTest() {
        try {
            topicDao.createWithGeneratedDate(topic);
            topic.setDate(null);
            Topic actual = topicDao.findTopicByTitle(BEFORE_METHOD_TOPIC_TITLE);
            Assert.assertNotNull(actual.getDate());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAllTest() {
        try {
            topicDao.create(topic);
            topicDao.create(new Topic("title2",
                    "text2",
                    LocalDateTime.of(2000, 3, 2, 20, 15),
                    new Account(1),
                    false,
                    false));
            List<Topic> actual = topicDao.findAll();

            Assert.assertEquals(actual.size(), 3);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findEntityByIdTest() {
        try {
            topicDao.create(topic);
            Topic topicByTitle = topicDao.findTopicByTitle(BEFORE_METHOD_TOPIC_TITLE);

            Topic actual = topicDao.findEntityById(topicByTitle.getTopicId());

            Assert.assertEquals(actual.getTopicId(), topicByTitle.getTopicId());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createTest() {
        try {
            boolean result = topicDao.create(topic);

            Assert.assertTrue(result);

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void updateTest() {
        try {
            topicDao.create(topic);
            Topic topicToUpdate = topicDao.findTopicByTitle(BEFORE_METHOD_TOPIC_TITLE);
            topicToUpdate.setText("text2");

            int actual = topicDao.update(topicToUpdate);
            Assert.assertEquals(actual, 1);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @AfterClass
    public void cleanUp() {
        try {
            DatabaseTestUtil.deregisterDrivers();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
