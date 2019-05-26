package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that implements CRUD and several other operations to work with database messages.
 */
class MessageDaoImpl extends AbstractDao<Message> implements MessageDao {

    private static final String FIND_ALL_MESSAGES = "SELECT message_id, message, account_id, date_posted, topic_id FROM message";
    private static final String FIND_MESSAGE_BY_ID = "SELECT message, account_id, date_posted, topic_id FROM message WHERE message_id = ?";
    private static final String INSERT_MESSAGE = "INSERT INTO message(message, account_id, date_posted, topic_id) VALUES(?, ?, ?, ?)";
    private static final String INSERT_MESSAGE_GENERATED_DATE = "INSERT INTO message(message, account_id, date_posted, topic_id) VALUES(?, ?, now(), ?)";
    private static final String DELETE_MESSAGE_BY_ID = "DELETE FROM message where message_id = ?";
    private static final String UPDATE_MESSAGE_BY_ID = "UPDATE message SET message = ?, account_id = ?, date_posted = ?, topic_id = ? " +
            "WHERE message_id = ?";
    private static final String FIND_MESSAGES_BY_TOPIC_ID = "SELECT message_id, message, account_id, " +
            "date_posted, topic_id FROM message WHERE topic_id = ?";
    private static final String COUNT_MESSAGES_WITH_CHOSEN_TOPIC = "SELECT COUNT(message_id) FROM message WHERE topic_id = ?";
    private static final String FIND_MESSAGES_IN_RANGE_SORT_BY_DATE = "SELECT message_id, message, account_id, date_posted, topic_id " +
            "FROM message WHERE topic_id = ? ORDER BY date_posted LIMIT ? OFFSET ?";

    /**
     * Creates MessageDaoImpl using chosen connection manager.
     * @param connectionManager Connection manager that provides connection to the DAO
     */
    public MessageDaoImpl(AbstractConnectionManager connectionManager) {
        super(connectionManager);
    }

    /**
     * Find all topic page messages.
     * @param topicId Id of the topic
     * @param startPage Page to get messages from
     * @param numberOfMessagesPerPage Number of messages per page
     * @return All page messages
     * @throws PersistenceException If SQLException or IOException is thrown
     */
    @Override
    public List<Message> findPageMessages(long topicId, int startPage, int numberOfMessagesPerPage) throws PersistenceException {
        List<Message> messageList = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_MESSAGES_IN_RANGE_SORT_BY_DATE)){
            statement.setLong(1, topicId);
            statement.setInt(2, numberOfMessagesPerPage);
            statement.setInt(3, (startPage - 1) * numberOfMessagesPerPage);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long messageId = resultSet.getLong(1);
                    Clob textClob = resultSet.getClob(2);
                    long accountId = resultSet.getLong(3);
                    LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                    long resultSetTopicId = resultSet.getLong(5);
                    String text;
                    try (Reader titleReader = textClob.getCharacterStream()) {
                        text = IOUtils.toString(titleReader);
                    }
                    messageList.add(new Message(messageId, text, new Account(accountId), date, new Topic(resultSetTopicId)));
                }
            } catch (IOException e) {
                throw new PersistenceException("IOException while trying to read text clob");
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding page messages", e);
        }
        return messageList;
    }

    /**
     * Counts topic messages.
     * @param topicId Id of the topic
     * @return Topic message count
     * @throws PersistenceException If SQLException is thrown
     */
    public int countMessagesByTopicId(long topicId) throws PersistenceException {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(COUNT_MESSAGES_WITH_CHOSEN_TOPIC)) {
            preparedStatement.setLong(1, topicId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
        return 0;
    }

    /**
     * Find messages by topic id.
     * @param topicId Id of the topic
     * @return Messages found by topic id
     * @throws PersistenceException If SQLException or IOException is thrown
     */
    public List<Message> findMessagesByTopicId(long topicId) throws PersistenceException {
        List<Message> list = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_MESSAGES_BY_TOPIC_ID)) {
            statement.setLong(1, topicId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long messageId = resultSet.getLong(1);
                    Clob textClob = resultSet.getClob(2);
                    long accountId = resultSet.getLong(3);
                    LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                    long resultSetTopicId = resultSet.getLong(5);
                    String text;
                    try (Reader titleReader = textClob.getCharacterStream()) {
                        text = IOUtils.toString(titleReader);
                    }
                    list.add(new Message(messageId, text, new Account(accountId), date, new Topic(resultSetTopicId)));
                }
            } catch (IOException e) {
                throw new PersistenceException("IOException while working with clob reader", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while executing findFromPositionByTopicId", e);
        }
        return list;
    }

    /**
     * Adds message to the database with date generated by the database.
     * @param entity Message to add to the database
     * @return {@code true} if message was successfully added, else returns {@code false}
     * @throws PersistenceException if SQLException is thrown
     */
    public boolean createWithGeneratedDate(Message entity) throws PersistenceException {
        if (entity == null) {
            return false;
        }
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE_GENERATED_DATE)){
            Clob textClob = connection.createClob();
            textClob.setString(1, entity.getText());
            statement.setClob(1, textClob);
            Account account = entity.getAccount();
            if (account == null) {
                return false;
            }
            statement.setLong(2, account.getAccountId());
            Topic topic = entity.getTopic();
            if (topic == null) {
                return false;
            }
            statement.setLong(3, topic.getTopicId());
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while creating message with generated date", e);
        }
    }

    /**
     * Finds all messages from the database.
     * @return All database messages
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public List<Message> findAll() throws PersistenceException {
        List<Message> list = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ALL_MESSAGES);
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                long messageId = resultSet.getLong(1);
                Clob textClob = resultSet.getClob(2);
                long accountId = resultSet.getLong(3);
                LocalDateTime date = resultSet.getObject(4, LocalDateTime.class);
                long topicId = resultSet.getLong(5);

                String text;
                try (Reader titleReader = textClob.getCharacterStream()) {
                    text = IOUtils.toString(titleReader);
                }

                list.add(new Message(messageId, text, new Account(accountId), date, new Topic(topicId)));
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while executing findAll", e);
        } catch (IOException e) {
            throw new PersistenceException("IOException while working with clob reader", e);
        }
        return list;
    }

    /**
     * Finds message by id
     * @param id Id of the message to find
     * @return Message if it was successfully found, else returns null
     * @throws PersistenceException If SQLException or IOException is thrown
     */
    @Override
    public Message findEntityById(long id) throws PersistenceException {
        Message message = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_MESSAGE_BY_ID)){
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Clob textClob = resultSet.getClob(1);
                    long accountId = resultSet.getLong(2);
                    LocalDateTime date = resultSet.getObject(3, LocalDateTime.class);
                    long topicId = resultSet.getLong(4);

                    String text;
                    try (Reader titleReader = textClob.getCharacterStream()) {
                        text = IOUtils.toString(titleReader);
                    }

                    message = new Message(id, text, new Account(accountId), date, new Topic(topicId));
                }
            } catch (IOException e) {
                throw new PersistenceException("IOException while working with clob reader", e);
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding by id", e);
        }
        return message;
    }

    /**
     * Returns query that deletes message by id.
     * @return query that deletes message by id
     */
    @Override
    public String getDeleteByIdQuery() {
        return DELETE_MESSAGE_BY_ID;
    }

    /**
     * Adds message to the database.
     * @param entity Entity to add
     * @return {@code true} if message was successfully added, else returns {@code false}
     * @throws PersistenceException If SQLException thrown
     */
    @Override
    public boolean create(Message entity) throws PersistenceException {
        if (entity == null) {
            return false;
        }
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE)){
            Clob textClob = connection.createClob();
            textClob.setString(1, entity.getText());
            statement.setClob(1, textClob);
            Account account = entity.getAccount();
            if (account == null) {
                return false;
            }
            statement.setLong(2, account.getAccountId());
            statement.setObject(3, entity.getDate());
            Topic topic = entity.getTopic();
            if (topic == null) {
                return false;
            }
            statement.setLong(4, topic.getTopicId());
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while creating", e);
        }
    }

    /**
     * Updates database message.
     * @param entity Entity to update
     * @return {@code true} if message was successfully updated, else returns {@code false}
     * @throws PersistenceException If SQLException thrown
     */
    @Override
    public int update(Message entity) throws PersistenceException {
        if (entity == null) {
            logger.log(Level.INFO, "cannot update message: entity is null");
            return 0;
        }
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_MESSAGE_BY_ID)){
            Clob textClob = connection.createClob();
            textClob.setString(1, entity.getText());
            statement.setClob(1, textClob);
            Account account = entity.getAccount();
            if (account == null) {
                logger.log(Level.INFO, "cannot update message: account is null");
                return 0;
            }
            statement.setLong(2, account.getAccountId());
            statement.setObject(3, entity.getDate());
            Topic topic = entity.getTopic();
            if (topic == null) {
                logger.log(Level.INFO, "cannot update message: topic is null");
                return 0;
            }
            statement.setLong(4, topic.getTopicId());
            statement.setLong(5, entity.getMessageId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while updating", e);
        }
    }
}
