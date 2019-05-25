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
     * Enum which contains constants for {@link RegistrationService#registerAccount(String, String, String)} to return.
     */
    public enum RegistrationResult {
        SUCCESS,
        EMAIL_EXISTS,
        USERNAME_EXISTS,
        CANNOT_CREATE_ACCOUNT_IN_DATABASE,
        ARGUMENT_IS_NULL
    }

    /**
     * Register account, storing it's data in the database.
     * If account with chosen email or username already exists, returns {@link RegistrationResult#EMAIL_EXISTS} or
     * {@link RegistrationResult#USERNAME_EXISTS} respectively.
     * @param username Accounts username
     * @param password Accounts password
     * @param email Accounts email. Registration of the account with email which is already used by another account is not permitted
     * @return {@link RegistrationResult} constant based on the registration result.
     * @throws ServiceException If PersistenceException thrown
     */
    public RegistrationResult registerAccount(String username, String password, String email) throws ServiceException {
        if (username == null) {
            logger.log(Level.WARN, "cannot register account, username is null");
            return RegistrationResult.ARGUMENT_IS_NULL;
        }
        if (password == null) {
            logger.log(Level.WARN, "cannot register account, password is null");
            return RegistrationResult.ARGUMENT_IS_NULL;
        }
        if (email == null) {
            logger.log(Level.WARN, "cannot register account, email is null");
            return RegistrationResult.ARGUMENT_IS_NULL;
        }
        RegistrationResult result;
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            logger.log(Level.TRACE, "autocommit disabled");
            try {
                AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                Account accountInDatabaseByEmail = accountDao.findAccountByEmail(email);

                if (accountInDatabaseByEmail == null) {
                    Account accountInDatabaseByUsername = accountDao.findAccountByUsername(username);
                    if (accountInDatabaseByUsername == null) {
                        HashGenerator hashGenerator = hashGeneratorFactory.createHashGenerator();
                        SaltGenerator saltGenerator = new SaltGenerator();
                        String salt = saltGenerator.generateSalt();
                        String passwordHash = hashGenerator.hash(password, salt, ApplicationConstants.HASHING_ALGORITHM);
                        Account account = new Account(username, passwordHash, email, AccountType.USER, DEFAULT_RATING,
                                DEFAULT_VERIFIED, DEFAULT_BLOCKED, salt, null);
                        if (accountDao.create(account)) {
                            logger.log(Level.INFO, "account with email=" + email + " added to database");
                            connectionManager.commit();
                            result = RegistrationResult.SUCCESS;
                        } else {
                            logger.log(Level.INFO, "could not create new account with email=" + email +
                                    " and username= " + username + ". Account cannot be added to the database");
                            connectionManager.rollback();
                            result = RegistrationResult.CANNOT_CREATE_ACCOUNT_IN_DATABASE;
                        }
                    } else {
                        logger.log(Level.INFO, "cannot create new account with email=" + email +
                                " and username= " + username + " because username is already in use");
                        connectionManager.rollback();
                        result = RegistrationResult.USERNAME_EXISTS;
                    }
                } else {
                    logger.log(Level.INFO, "cannot create new account with email=" + email +
                            " and username= " + username + " because email is already in use");
                    connectionManager.rollback();
                    result = RegistrationResult.EMAIL_EXISTS;
                }
            } catch (PersistenceException e) {
                connectionManager.rollback();
                throw new ServiceException("Unable to execute transaction. Transaction rolled back", e);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return result;
    }
}
