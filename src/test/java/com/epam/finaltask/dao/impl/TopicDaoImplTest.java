package com.epam.finaltask.dao.impl;

import com.epam.finaltask.entity.Account;
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

public class TopicDaoImplTest {

    private static final String BEFORE_METHOD_TOPIC_TITLE = "bftitle";
    private static final String BEFORE_METHOD_TOPIC_TEXT = "bftext";
    private TopicDaoImpl topicDao;
    private Topic topic;
    @Mock
    AbstractConnectionManager connectionManager;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        try {
            DatabaseTestUtil.registerDrivers();
            DatabaseTestUtil.initializeDatabase();
            Connection connection = DatabaseTestUtil.getConnection();
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
            boolean result = topicDao.createWithGeneratedDate(topic);
            Assert.assertTrue(result);
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
            long topicId = -1;
            String title = "new Title";
            topic.setTitle(title);
            topicDao.create(topic);

            List<Topic> topics = topicDao.findAll();
            for (Topic topic : topics) {
                if (title.equals(topic.getTitle())) {
                    topicId = topic.getTopicId();
                }
            }
            if (topicId == -1) {
                fail("unable to find created topic to define topicId");
            }

            Topic actual = topicDao.findEntityById(topicId);

            Assert.assertEquals(actual.getTopicId(), topicId);
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
            long topicId = -1;
            String title = "new Title2";
            topic.setTitle(title);
            topicDao.create(topic);

            List<Topic> topics = topicDao.findAll();
            for (Topic topic : topics) {
                if (title.equals(topic.getTitle())) {
                    topicId = topic.getTopicId();
                }
            }
            if (topicId == -1) {
                fail("unable to find created topic to define topicId");
            }
            Topic topicToUpdate = topicDao.findEntityById(topicId);
            topicToUpdate.setText("text22");

            int actual = topicDao.update(topicToUpdate);
            Assert.assertEquals(actual, 1);
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
