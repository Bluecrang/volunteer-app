package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;
import com.epam.finaltask.util.impl.HashGeneratorFactoryImpl;
import com.epam.finaltask.util.impl.SaltGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class which provides registration logic.
 */
public class RegistrationService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();

    private static final int DEFAULT_RATING = 0;
    private static final boolean DEFAULT_BLOCKED = false;
    private static final boolean DEFAULT_VERIFIED = false;
    /**
     * Factory which is used to generate password hashes.
     */
    private HashGeneratorFactory hashGeneratorFactory;

    /**
     * Creates RegistrationService with chosen DaoFactory, connectionManagerFactory, hashGeneratorFactory.
     * @param daoFactory Factory which is used to create DAO objects
     * @param connectionManagerFactory Factory which is used to create {@link com.epam.finaltask.dao.impl.AbstractConnectionManager} subclass instances
     * @param hashGeneratorFactory Factory which is used to create {@link HashGenerator} implementations
     */
    public RegistrationService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory,
                               HashGeneratorFactory hashGeneratorFactory) {
        super(daoFactory, connectionManagerFactory);
        this.hashGeneratorFactory = hashGeneratorFactory;
    }

    /**
     * Creates RegistrationService with {@link DaoFactory} and {@link ConnectionManagerFactory}
     * defined in {@link AbstractService} no-argument constructor and {@link HashGeneratorFactoryImpl} as HashGeneratorFactory.
     */
    public RegistrationService() {
        super();
        this.hashGeneratorFactory = new HashGeneratorFactoryImpl();
    }

    /**
     * Register account, storing it's data in the database.
     * If account with chosen email or username already exists, returns {@code false}.
     * @param username Accounts username
     * @param password Accounts password
     * @param email Accounts email. Registration of the account with email which is already used by another account is not permitted
     * @return {@code true} if account successfully registered, else returns {@code false}
     * @throws ServiceException If PersistenceException thrown
     */
    public boolean registerAccount(String username, String password, String email) throws ServiceException { //todo unique username
        if (username == null) {
            logger.log(Level.WARN, "cannot register account, username is null");
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
                    Account account = new Account(username, passwordHash, email, AccountType.USER, DEFAULT_RATING,
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
