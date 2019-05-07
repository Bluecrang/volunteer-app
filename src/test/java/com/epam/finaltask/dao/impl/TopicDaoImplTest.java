package com.epam.finaltask.dao.impl;

import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Topic;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class TopicDaoImplTest {

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
        topic = new Topic("title",
            "text",
                LocalDateTime.of(2000, 2, 15, 21, 54),
            new Account(1),
            false,
            false);
    }

    @Test
    public void createWithGeneratedDateTest() {
        try {
            topicDao.createWithGeneratedDate(topic);
            Topic actual = topicDao.findTopicByTitle("title");
            topicDao.delete(actual.getTopicId());
            Assert.assertEquals(actual.getText(), topic.getText());
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
            for (Topic databaseTopic : actual) {
                topicDao.delete(databaseTopic.getTopicId());
            }
            Assert.assertEquals(actual.size(), 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findEntityByIdTest() {
        try {
            topicDao.create(topic);
            Topic topicByTitle = topicDao.findTopicByTitle("title");

            Topic actual = topicDao.findEntityById(topicByTitle.getTopicId());
            topicDao.delete(topicByTitle.getTopicId());

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

            Topic topicByTitle = topicDao.findTopicByTitle("title");
            topicDao.delete(topicByTitle.getTopicId());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void updateTest() {
        try {
            topicDao.create(topic);
            Topic topicToUpdate = topicDao.findTopicByTitle("title");
            topicToUpdate.setText("text2");

            topicDao.update(topicToUpdate);

            Topic actual = topicDao.findEntityById(topicToUpdate.getTopicId());
            topicDao.delete(actual.getTopicId());

            Assert.assertEquals(actual.getText(), "text2");
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
