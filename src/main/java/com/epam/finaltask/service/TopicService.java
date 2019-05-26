package com.epam.finaltask.service;

import com.epam.finaltask.dao.*;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Message;
import com.epam.finaltask.entity.Topic;
import com.epam.finaltask.util.ApplicationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Service class that provides methods to add and manipulate database topics.
 */
public class TopicService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Creates TopicService with chosen DaoFactory and ConnectionManagerFactory implementations.
     * @param daoFactory Factory that is used to create DAO objects
     * @param connectionManagerFactory Factory that is used to create {@link AbstractConnectionManager} subclass instances
     */
    public TopicService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory) {
        super(daoFactory, connectionManagerFactory);
    }

    /**
     * Creates TopicService with with default DaoFactory and ConnectionManagerFactory
     * defined in {@link AbstractService} no-arguments constructor.
     */
    public TopicService() {
        super();
    }

    /** todo
     * Changes topic closed flag to {@code true}.
     * @param topicId Id of the topic to close
     * @return {@code true} if topic successfully closed, else returns {@code false}
     * @throws ServiceException if PersistenceException is thrown while working with database
     */
    public boolean closeTopic(long topicId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
                Topic topic = topicDao.findEntityById(topicId);
                if (topic == null) {
                    logger.log(Level.WARN, "could not find topic to close");
                    return false;
                }
                topic.setClosed(true);
                topicDao.update(topic);
                MessageDao messageDao = daoFactory.createMessageDao(connectionManager);
                List<Message> messageList = messageDao.findMessagesByTopicId(topic.getTopicId());
                AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                List<Account> accountList = messageList.stream()
                        .map(Message::getAccount)
                        .distinct()
                        .filter(account -> account.getAccountId() != topic.getAccount().getAccountId())
                        .collect(Collectors.toList());
                for (Account account : accountList) {
                    Account fetchedAccount = accountDao.findEntityById(account.getAccountId());
                    if (fetchedAccount != null) {
                        logger.log(Level.DEBUG, "Fetched account id=" + fetchedAccount.getAccountId());
                        AccountType accountType = fetchedAccount.getAccountType();
                        if (accountType == AccountType.ADMIN || accountType == AccountType.VOLUNTEER) {
                            fetchedAccount.setRating(fetchedAccount.getRating() + ApplicationConstants.TOPIC_CLOSE_RATING_BONUS);
                            accountDao.update(fetchedAccount);
                        }
                    }
                }
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

    /**
     * Sets topic {@code hidden} flag to the chosen value.
     * @param topicId Id of the topic which flag should be set
     * @param hide State to set to the topic's {@code hidden} flag
     * @return {@code true} if topic's {@code hidden} flag was set, else returns {@code false}
     * @throws ServiceException if PersistenceException is thrown while working with database
     */
    public boolean changeTopicHiddenState(long topicId, boolean hide) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
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
                throw new ServiceException("Could not finish transaction. Transaction rolled back", e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Creates topic with chosen account, title and text and adds it to the database.
     * @param account Account of the topic creator
     * @param title Title of the topic to create
     * @param text Text of the topic to create
     * @return {@code true} if topic was successfully created and added to the database. Else returns null
     * @throws ServiceException if PersistenceException is thrown while working with database
     */
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
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            Topic topic = new Topic();
            topic.setTitle(title);
            topic.setText(text);
            topic.setAccount(account);
            TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
            return topicDao.createWithGeneratedDate(topic);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Looks for topic in the database by title.
     * @param topicId Id of the topic to look for
     * @return Topic with chosen id, if it was found. Else returns null
     * @throws ServiceException if PersistenceException is thrown while working with database
     */
    public Topic findTopicById(long topicId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
                Topic topic = topicDao.findEntityById(topicId);
                AccountService accountService = new AccountService(daoFactory, connectionManagerFactory);
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

    /**
     * Returns all topics from the database sorted by date descending.
     * @return All database topics sorted by date descending
     * @throws ServiceException if PersistenceException is thrown while working with database
     */
    public List<Topic> findAllTopics() throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
                AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                List<Topic> topics = topicDao.findAll();
                for (Topic topic : topics) {
                    Account account = accountDao.findEntityById(topic.getAccount().getAccountId());
                    topic.setAccount(account);
                }
                connectionManager.commit();
                topics.sort(Comparator.comparing(Topic::getDate, Comparator.reverseOrder()));
                return topics;
            }  catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException(e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    //todo
    public List<Topic> findTopicsByTitleSubstring(Account sessionAccount, String searchString) throws ServiceException {
        if (searchString == null) {
            logger.log(Level.WARN, "unable to find topics by title searchString: searchString is null");
            return Collections.emptyList();
        }
        if (sessionAccount == null) {
            logger.log(Level.WARN, "unable to find topics by title searchString: sessionAccount is null");
            return Collections.emptyList();
        }
        AccountType accountType = sessionAccount.getAccountType();
        List<Topic> topics;
        if (accountType == AccountType.ADMIN || accountType == AccountType.VOLUNTEER) {
            topics = findAllTopics();
        } else if (accountType == AccountType.USER) {
            topics = findTopicsByAuthorId(sessionAccount.getAccountId());
        } else {
            topics = Collections.emptyList();
        }
        return topics.stream()
                .filter((topic -> {
                    String title = topic.getTitle();
                    if (title != null) {
                        boolean result = title.contains(searchString);
                        logger.log(Level.DEBUG, "title = " + title);
                        logger.log(Level.DEBUG, "searching result = " + result);
                        return result;
                    }
                    return false;
                }))
                .collect(Collectors.toList());
    }

    /**
     * Finds all topics by authors Account id.
     * @param authorId Id of the account
     * @return List of topics with chosen author id
     * @throws ServiceException If PersistenceException is thrown
     */
    public List<Topic> findTopicsByAuthorId(long authorId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            TopicDao topicDao = daoFactory.createTopicDao(connectionManager);
            return topicDao.findTopicsByAccountId(authorId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
