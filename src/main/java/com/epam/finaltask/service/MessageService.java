package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MessageService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();

    public MessageService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory) {
        super(daoFactory, connectionManagerFactory);
    }

    public MessageService() {
        super();
    }

    public List<Message> findTopicPageMessages(long topicId, int currentPage, int numberOfMessagesPerPage)
            throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
            return messageDao.findPageMessages(topicId, currentPage, numberOfMessagesPerPage);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public int countMessages(long topicId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
            return messageDao.countMessagesByTopicId(topicId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Message findMessageById(long id) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
            return messageDao.findEntityById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean deleteMessage(Account account, long messageId) throws ServiceException {
        if (account == null) {
            logger.log(Level.WARN, "cannot delete message id=" + messageId + " because account is null");
            return false;
        }
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
                if (account.getAccessLevel().equals(AccessLevel.ADMIN)) {
                    messageDao.delete(messageId);
                    connectionManager.commit();
                    logger.log(Level.INFO, "message id=" + messageId + " successfully deleted");
                    return true;
                }
                connectionManager.rollback();
            } catch (PersistenceException e) {
                connectionManager.rollback();
                logger.log(Level.DEBUG, "unable to delete message, transaction is rolled back");
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return false;
    }

    public boolean createMessage(Account account, long topicId, String text) throws ServiceException {
        if (account == null) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because account is null");
            return false;
        }
        if (text == null || StringUtils.isBlank(text)) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because text is null or blank");
            return false;
        }
        if (text.length() > 256) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because text more than 256 characters long");
            return false;
        }
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            Message message = new Message();
            message.setMessage(text);
            message.setAccount(account);
            message.setTopic(new Topic(topicId));
            MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
            return messageDao.createWithGeneratedDate(message);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<Message> findMessagesByTopicId(long topicId) throws ServiceException {  //TODO remove?
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
                List<Message> messageList = messageDao.findMessagesByTopicId(topicId);
                AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                for (Message message : messageList) {
                    Account currentAccount = accountDao.findEntityById(message.getAccount().getAccountId());
                    message.setAccount(currentAccount);
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
}
