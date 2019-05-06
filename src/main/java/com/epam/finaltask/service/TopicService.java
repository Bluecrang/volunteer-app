package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.TopicDao;
import com.epam.finaltask.dao.impl.AccountDaoImpl;
import com.epam.finaltask.dao.impl.ConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.dao.impl.TopicDaoImpl;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.Topic;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TopicService {

    private static final Logger logger = LogManager.getLogger();

    public boolean closeTopic(Account closingAccount, long topicId) throws ServiceException {
        if (closingAccount == null) {
            logger.log(Level.WARN, "unable to close topic: closing account is null");
            return false;
        }
        if (!closingAccount.getAccessLevel().equals(AccessLevel.ADMIN)) {
            logger.log(Level.WARN, "unable to close topic: account id=" + closingAccount.getAccountId() + " does not have rights to close topic");
            return false;
        }
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = new TopicDaoImpl(connectionManager);
                Topic topic = topicDao.findEntityById(topicId);
                if (topic == null) {
                    logger.log(Level.WARN, "could not find topic to close");
                    return false;
                }
                topic.setClosed(true);
                topicDao.update(topic);
                connectionManager.commit();
                return true;
            } catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean changeTopicHiddenState(Account account, long topicId, boolean hide) throws ServiceException {
        if (account == null) {
            logger.log(Level.WARN, "cannot hide topic: account is null, topicId=" + topicId);
            return false;
        }
        if (account.getAccessLevel().equals(AccessLevel.ADMIN)) {
            try (ConnectionManager connectionManager = new ConnectionManager()) {
                connectionManager.disableAutoCommit();
                try {
                    TopicDao topicDao = new TopicDaoImpl(connectionManager);
                    Topic topic = topicDao.findEntityById(topicId);
                    if (topic == null) {
                        logger.log(Level.WARN, "could not find topic to hide, topicId=" + topicId);
                        return false;
                    }
                    topic.setHidden(hide);
                    topicDao.update(topic);
                    connectionManager.commit();
                    return true;
                } catch (PersistenceException e) {
                    connectionManager.rollback();
                    throw new ServiceException(e);
                }
            } catch (PersistenceException e) {
                throw new ServiceException(e);
            }
        }
        return false;
    }

    public Topic findTopicByTitle(String title) throws ServiceException {
        if (title == null) {
            return null;
        }
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = new TopicDaoImpl(connectionManager);
                Topic topic = topicDao.findTopicByTitle(title);
                if (topic == null) {
                    return null;
                }
                AccountService accountService = new AccountService();
                Account account = accountService.findAccountById(topic.getAccount().getAccountId(), connectionManager);
                connectionManager.commit();
                if (account == null) {
                    throw new ServiceException("could not find topic creator's account");
                }
                topic.setAccount(account);
                return topic;
            } catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean createTopic(Account account, String title, String text) throws ServiceException {
        if (account == null) {
            logger.log(Level.WARN, "account is null, cannot create topic");
            return false;
        }
        if (title == null || StringUtils.isBlank(title)) {
            logger.log(Level.WARN, "title is null or blank, cannot create topic");
            return false;
        }
        if (text == null || StringUtils.isBlank(text)) {
            logger.log(Level.WARN, "text is null or blank, cannot create topic");
            return false;
        }
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            Topic topic = new Topic();
            topic.setTitle(title);
            topic.setText(text);
            topic.setAccount(account);
            TopicDao topicDao = new TopicDaoImpl(connectionManager);
            topicDao.createWithGeneratedDate(topic);
            return true;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Topic findTopicById(long topicId) throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = new TopicDaoImpl(connectionManager);
                Topic topic = topicDao.findEntityById(topicId);
                AccountService accountService = new AccountService();
                Account account = accountService.findAccountById(topic.getAccount().getAccountId(), connectionManager);
                connectionManager.commit();
                topic.setAccount(account);
                return topic;
            } catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }

        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<Topic> findAllTopics() throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = new TopicDaoImpl(connectionManager);
                AccountDao accountDao = new AccountDaoImpl(connectionManager);
                List<Topic> topics = topicDao.findAll();
                for (Topic topic : topics) {
                    Account account = accountDao.findEntityById(topic.getAccount().getAccountId());
                    topic.setAccount(account);
                }
                connectionManager.commit();
                return topics;
            }  catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
