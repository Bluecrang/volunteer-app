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

import static org.mockito.Mockito.when;

public class TopicDaoImplTest {

    private static final String BEFORE_METHOD_TOPIC_TITLE = "bftitle";
    private static final String BEFORE_METHOD_TOPIC_TEXT = "bftext";
    private TopicDaoImpl topicDao;
    private Topic topic;
    @Mock
    AbstractConnectionManager connectionManager;
    Connection connection;

    @BeforeClass
    public void init() throws SQLException {
        DatabaseTestUtil.registerDrivers();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws SQLException {
        DatabaseTestUtil.deregisterDrivers();
    }

    @BeforeMethod
    public void initBeforeMethod() throws SQLException, IOException {
        MockitoAnnotations.initMocks(this);
        DatabaseTestUtil.initializeDatabase();
        connection = DatabaseTestUtil.getConnection();
        when(connectionManager.getConnection()).thenReturn(connection);
        topicDao = new TopicDaoImpl(connectionManager);
        topic = new Topic(BEFORE_METHOD_TOPIC_TITLE,
                BEFORE_METHOD_TOPIC_TEXT,
                LocalDateTime.of(2000, 2, 15, 21, 54),
                new Account(1),
                false,
                false);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpDatabaseTopics() throws IOException, SQLException {
        DatabaseTestUtil.dropSchema();
    }

    @Test
    public void createWithGeneratedDate_validTopic_true() throws PersistenceException {
        boolean result = topicDao.createWithGeneratedDate(topic);

        Assert.assertTrue(result);
    }

    @Test
    public void findAll_threeTopicsExist_threeTopics() throws PersistenceException {
        topicDao.create(topic);
        topicDao.create(new Topic("title2",
                "text2",
                LocalDateTime.of(2000, 3, 2, 20, 15),
                new Account(1),
                false,
                false));

        List<Topic> actual = topicDao.findAll();

        Assert.assertEquals(actual.size(), 3);
    }

    @Test
    public void findEntityById_topicExists_topicFound() throws PersistenceException {
        long topicId = 2;
        String title = "new Title";
        topic.setTitle(title);
        topicDao.create(topic);

        Topic actual = topicDao.findEntityById(topicId);

        Assert.assertEquals(actual.getTopicId(), topicId);
    }

    @Test
    public void create_validTopic_true() throws PersistenceException {
        boolean result = topicDao.create(topic);

        Assert.assertTrue(result);
    }

    @Test
    public void update_validUpdateData_true() throws PersistenceException {
        long topicId = 2;
        String title = "new Title";
        topic.setTitle(title);
        topicDao.create(topic);

        Topic topicToUpdate = topicDao.findEntityById(topicId);
        topicToUpdate.setText("text22");

        int actual = topicDao.update(topicToUpdate);

        Assert.assertEquals(actual, 1);
    }

    @Test
    public void findPageTopics_twoTopicsLongPageFourTopicsExist_twoTopicsFound() throws PersistenceException {
        topicDao.create(topic);
        topicDao.create(new Topic("title12",
                "text12",
                LocalDateTime.of(2001, 7, 15, 22, 44),
                new Account(1),
                false,
                false));
        topicDao.create(new Topic("title13",
                "text13",
                LocalDateTime.of(2020, 5, 3, 1, 54),
                new Account(1),
                false,
                false));

        List<Topic> actual = topicDao.findPageTopics(1, 2, true);

        Assert.assertEquals(actual.size(), 2);
    }
}
