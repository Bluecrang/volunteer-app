package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.MessageDao;
import com.epam.finaltask.dao.impl.AccountDaoImpl;
import com.epam.finaltask.dao.impl.ConnectionManager;
import com.epam.finaltask.dao.impl.MessageDaoImpl;
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

public class MessageService {

    private static final Logger logger = LogManager.getLogger();

    public List<Message> findTopicPageMessages(long topicId, int currentPage, int numberOfMessagesPerPage)
            throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            MessageDao messageDao = new MessageDaoImpl(connectionManager);
            return messageDao.findPageMessages(topicId, currentPage, numberOfMessagesPerPage);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public int countMessages(long topicId) throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            MessageDao messageDao = new MessageDaoImpl(connectionManager);
            return messageDao.countMessagesByTopicId(topicId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Message findMessageById(long id) throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            MessageDao messageDao = new MessageDaoImpl(connectionManager);
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
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                MessageDao messageDao = new MessageDaoImpl(connectionManager);
                Message message = messageDao.findEntityById(messageId);
                if (message == null) {
                    connectionManager.rollback();
                    logger.log(Level.WARN, "could not find message id=" + messageId + " to delete");
                    return false;
                }
                if (account.getAccessLevel().equals(AccessLevel.ADMIN)) {
                    messageDao.delete(message.getMessageId());
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
        if (StringUtils.isBlank(text)) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because text is blank");
            return false;
        }
        if (text.length() > 256) {
            logger.log(Level.WARN, "cannot create message in topic id=" + topicId + " because text more than 256 characters long");
        }
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            Message message = new Message();
            message.setMessage(text);
            message.setAccount(account);
            message.setTopic(new Topic(topicId));
            MessageDao messageDao = new MessageDaoImpl(connectionManager);
            return messageDao.createWithGeneratedDate(message);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<Message> findMessagesByTopicId(long topicId) throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                MessageDao messageDao = new MessageDaoImpl(connectionManager);
                List<Message> messageList = messageDao.findMessagesByTopicId(topicId);
                AccountDao accountDao = new AccountDaoImpl(connectionManager);
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
