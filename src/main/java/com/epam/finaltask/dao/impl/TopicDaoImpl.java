package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.TopicDao;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Topic;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TopicDaoImpl extends AbstractDao<Topic> implements TopicDao {

    private static final String FIND_ALL_TOPICS = "SELECT topic_id, closed, title, text, date_posted, account_id, hidden " +
            "FROM topic";
    private static final String FIND_TOPIC_BY_ID = "SELECT closed, title, text, date_posted, account_id, hidden " +
            "FROM topic WHERE topic_id = ?";
    private static final String FIND_TOPIC_BY_TITLE = "SELECT topic_id, closed, text, date_posted, account_id, hidden " +
            "FROM topic WHERE title = ?";
    private static final String INSERT_TOPIC = "INSERT INTO topic(closed, title, text, date_posted, account_id, hidden) " +
            "VALUES(?,?,?,?,?,?)";
    private static final String INSERT_TOPIC_GENERATED_CURRENT_DATE =
            "INSERT INTO topic(closed, title, text, date_posted, account_id, hidden) VALUES(?,?,?,now(),?,?)";
    private static final String DELETE_TOPIC_BY_ID = "DELETE FROM topic where topic_id = ?";
    private static final String UPDATE_TOPIC_BY_ID = "UPDATE topic " +
            "SET closed = ?, title = ?, text = ?, date_posted = ?, account_id = ?, hidden = ? " +
            "WHERE topic_id = ?";

    public TopicDaoImpl(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    public boolean createWithGeneratedDate(Topic entity) throws PersistenceException {
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TOPIC_GENERATED_CURRENT_DATE)){
            statement.setBoolean(1, entity.isClosed());

            Clob titleClob = connection.createClob();
            titleClob.setString(1, entity.getTitle()); //todo
            statement.setClob(2, titleClob);

            Clob textClob = connection.createClob();
            textClob.setString(1, entity.getText());
            statement.setClob(3, textClob);

            statement.setLong(4, entity.getAccount().getAccountId());
            statement.setBoolean(5, entity.isHidden());
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while creating", e);
        }
    }

    public Topic findTopicByTitle(String title) throws PersistenceException {
        if (title == null) {
            return null;
        }
        Topic topic = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_TOPIC_BY_TITLE)){
            statement.setString(1, title);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long topicId = resultSet.getLong(1);
                    boolean closed = resultSet.getBoolean(2);
                    Clob textClob = resultSet.getClob(3);
                    LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                    long accountId = resultSet.getLong(5);
                    boolean hidden = resultSet.getBoolean(6);

                    Account account = new Account(accountId);

                    String text;
                    try (Reader textReader = textClob.getCharacterStream()) {
                        text = IOUtils.toString(textReader);
                    }
                    topic = new Topic(topicId, title, text, date, account, closed, hidden);
                }
            } catch (IOException e) {
                throw new PersistenceException("IOException while working with clob reader", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding by title", e);
        }
        return topic;
    }

    @Override
    public List<Topic> findAll() throws PersistenceException {
        List<Topic> list = new ArrayList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ALL_TOPICS)){
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long topicId = resultSet.getLong(1);
                    boolean closed = resultSet.getBoolean(2);
                    Clob titleClob = resultSet.getClob(3);
                    Clob textClob = resultSet.getClob(4);
                    LocalDateTime date = resultSet.getObject(5, LocalDateTime.class);
                    long accountId = resultSet.getLong(6);
                    boolean hidden = resultSet.getBoolean(7);

                    Account account = new Account(accountId);

                    String title;
                    try (Reader titleReader = titleClob.getCharacterStream()) {
                        title = IOUtils.toString(titleReader);
                    }

                    String text;
                    try (Reader textReader = textClob.getCharacterStream()) {
                        text = IOUtils.toString(textReader);
                    }

                    list.add(new Topic(topicId, title, text, date, account, closed, hidden));
                }
            } catch (IOException e) {
                throw new PersistenceException("IOException while working with clob reader", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while executing findAll", e);
        }
        return list;
    }

    @Override
    public Topic findEntityById(long id) throws PersistenceException {
        Topic topic = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_TOPIC_BY_ID)){
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    boolean closed = resultSet.getBoolean(1);
                    Clob titleClob = resultSet.getClob(2);
                    Clob textClob = resultSet.getClob(3);
                    LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                    long accountId = resultSet.getLong(5);
                    boolean hidden = resultSet.getBoolean(6);

                    Account account = new Account(accountId);

                    String title;
                    try (Reader titleReader = titleClob.getCharacterStream()) {
                        title = IOUtils.toString(titleReader);
                    }

                    String text;
                    try (Reader textReader = textClob.getCharacterStream()) {
                        text = IOUtils.toString(textReader);
                    }

                    topic = new Topic(id, title, text, date, account, closed, hidden);
                }
            } catch (IOException e) {
                throw new PersistenceException("IOException while working with clob reader", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding by id", e);
        }
        return topic;
    }

    @Override
    public String getDeleteByIdQuery() {
        return DELETE_TOPIC_BY_ID;
    }

    @Override
    public boolean create(Topic entity) throws PersistenceException {
        if (entity == null) {
            return false;
        }
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_TOPIC)){
            statement.setBoolean(1, entity.isClosed());

            Clob titleClob = connection.createClob();
            titleClob.setString(1, entity.getTitle());
            statement.setClob(2, titleClob);

            Clob textClob = connection.createClob();
            textClob.setString(1, entity.getText());
            statement.setClob(3, textClob);
            statement.setObject(4, entity.getDate());
            statement.setBoolean(5, entity.isHidden());
            Account account = entity.getAccount();
            if (account == null) {
                return false;
            }
            statement.setLong(5, account.getAccountId());
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while creating", e);
        }
    }

    @Override
    public int update(Topic entity) throws PersistenceException {
        if (entity == null) {
            return 0;
        }
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_TOPIC_BY_ID)){
            statement.setBoolean(1, entity.isClosed());

            Clob titleClob = connection.createClob();
            titleClob.setString(1, entity.getTitle());
            statement.setClob(2, titleClob);

            Clob textClob = connection.createClob();
            textClob.setString(1, entity.getText());
            statement.setClob(3, textClob);

            statement.setObject(4, entity.getDate());
            statement.setLong(5, entity.getAccount().getAccountId());
            statement.setBoolean(6, entity.isHidden());
            statement.setLong(7, entity.getTopicId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while updating", e);
        }
    }
}
