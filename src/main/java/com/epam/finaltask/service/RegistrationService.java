package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.util.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrationService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();
    private static final int DEFAULT_RATING = 0;
    private static final boolean DEFAULT_BLOCKED = false;
    private static final boolean DEFAULT_VERIFIED = false;
    private HashGeneratorFactory hashGeneratorFactory;

    public RegistrationService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory,
                               HashGeneratorFactory hashGeneratorFactory) {
        super(daoFactory, connectionManagerFactory);
        this.hashGeneratorFactory = hashGeneratorFactory;
    }

    public RegistrationService() {
        super();
        this.hashGeneratorFactory = new HashGeneratorFactoryImpl();
    }

    public boolean registerUser(String login, String password, String email) throws ServiceException { //todo javamail
        if (login == null) {
            logger.log(Level.WARN, "cannot register account, login is null");
            return false;
        }
        if (password == null) {
            logger.log(Level.WARN, "cannot register account, password is null");
            return false;
        }
        if (email == null) {
            logger.log(Level.WARN, "cannot register account, email is null");
            return false;
        }
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            logger.log(Level.TRACE, "autocommit disabled");
            try {
                AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                Account accountInDatabase = accountDao.findAccountByEmail(email);
                if (accountInDatabase == null) {
                    HashGenerator hashGenerator = hashGeneratorFactory.createHashGenerator();
                    SaltGenerator saltGenerator = new SaltGenerator();
                    String salt = saltGenerator.generateSalt();
                    String passwordHash = hashGenerator.hash(password, salt, ApplicationConstants.HASHING_ALGORITHM);
                    Account account = new Account(login, passwordHash, email, AccessLevel.USER, DEFAULT_RATING,
                            DEFAULT_VERIFIED, DEFAULT_BLOCKED, salt, null); //todo
                    accountDao.create(account);
                    logger.log(Level.INFO, "account with email=" + email + " added to database");
                    connectionManager.commit();
                    return true;
                }
                connectionManager.rollback();
                logger.log(Level.INFO, "cannot create new account with email=" + email + " because it already exists");
            } catch (PersistenceException e) {
                connectionManager.rollback();
                logger.log(Level.ERROR, "Unable to execute transaction. Transaction rolled back", e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return false;
    }
}
