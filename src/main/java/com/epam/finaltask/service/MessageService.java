package com.epam.finaltask.service;

import com.epam.finaltask.dao.*;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.validation.TextValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;

/**
 * Provides manipulate to create, delete and manipulate {@link Message} entities.
 */
public class MessageService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Creates MessageService using chosen {@link DaoFactory} and {@link ConnectionManagerFactory}.
     * @param daoFactory Factory used to create {@link com.epam.finaltask.dao.Dao} objects
     * @param connectionManagerFactory Factory used to create {@link AbstractConnectionManager} subclass object
     */
    public MessageService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory) {
        super(daoFactory, connectionManagerFactory);
    }

    /**
     * Creates MessageService with default factories defined in {@link AbstractService} no-arguments constructor.
     */
    public MessageService() {
        super();
    }

    /**
     * Retrieves {@link Message} list on the chosen page from topic with chosen id from the database.
     * Allows to get several topic messages sorted by date ascending.
     * Number of maximum returned messages specified using {@code numberOfMessagesPerPage} parameter.
     * Messages before chosen page are skipped, and won't be added to the result.
     * @param topicId Id of the topic which messages will be retrieved
     * @param currentPage Current page number. Only messages starting from chosen page will be retrieved
     * @param numberOfMessagesPerPage Number of messages on each page
     * @return List of messages
     * @throws ServiceException If PersistenceException is thrown while working with database
     */
    public List<Message> findTopicPageMessages(long topicId, int currentPage, int numberOfMessagesPerPage)
            throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
                List<Message> messageList = messageDao.findPageMessages(topicId, currentPage, numberOfMessagesPerPage);
                for (Message message : messageList) {
                    AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                    Account account = accountDao.findEntityById(message.getAccount().getAccountId());
                    message.setAccount(account);
                }
                connectionManager.commit();
                return messageList;
            } catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Returns database topic message count.
     * @param topicId Id of the topic which messages will be counted
     * @return Number of topic messages
     * @throws ServiceException If PersistenceException is thrown while working with database
     */
    public int countMessages(long topicId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
            return messageDao.countMessagesByTopicId(topicId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Retrieves message from the database by it's id. If message with chosen id does not exist, returns null.
     * @param id Id of the message to look for
     * @return Message with chosen id, if it exists in the database, else null
     * @throws ServiceException If PersistenceException is thrown while working with database
     */
    public Message findMessageById(long id) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
            return messageDao.findEntityById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Removes message from the database by id.
     * @param messageId Id of the message to be deleted
     * @return {@code true} if message successfully deleted, else returns {@code false}
     * @throws ServiceException If PersistenceException is thrown while working with database
     */
    public boolean deleteMessage(long messageId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
                Message message = messageDao.findEntityById(messageId);
                if (message == null) {
                    connectionManager.rollback();
                    logger.log(Level.WARN, "could not find message id=" + messageId + " to delete");
                    return false;
                }
                messageDao.delete(messageId);
                connectionManager.commit();
                logger.log(Level.INFO, "message id=" + messageId + " successfully deleted");
                return true;
            } catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Creates message with specified account, topicId and text and adds it to the database.
     * If topic that owns the message can't be found in the database or it is closed, then result is {@code false}.
     * @param account Account of the message author
     * @param topicId Id of the topic that owns the message
     * @param text Text of the message
     * @return {@code true} if message successfully created and added to the database, else returns {@code false}.
     * Returns {@code false} if topic that owns the message can't be found in the database or it is closed
     * @throws ServiceException If PersistenceException is thrown while working with database
     */
    public boolean createMessage(Account account, long topicId, String text) throws ServiceException {
        if (account == null) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because account is null");
            return false;
        }
        if (text == null || StringUtils.isBlank(text)) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because text is null or blank");
            return false;
        }
        TextValidator textValidator = new TextValidator();
        if (!textValidator.validate(text,256)) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because text more than 256 characters long");
            return false;
        }
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
            Topic topic = topicDao.findEntityById(topicId);
            if (topic != null && !topic.isClosed()) {
                Message message = new Message();
                message.setText(text);
                message.setAccount(account);
                message.setTopic(new Topic(topicId));
                MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
                return messageDao.createWithGeneratedDate(message);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return false;
    }
}
