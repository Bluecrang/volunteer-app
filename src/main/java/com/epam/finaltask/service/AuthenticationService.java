package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;
import com.epam.finaltask.util.impl.HashGeneratorFactoryImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides user authentication functionality.
 */
public class AuthenticationService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();
    /**
     * Generator used to generate password hash.
     */
    private HashGeneratorFactory hashGeneratorFactory;

    /**
     * Creates AuthenticationService with chosen {@link DaoFactory},
     * {@link ConnectionManagerFactory} and {@link HashGeneratorFactory}
     * @param daoFactory Factory used to create {@link com.epam.finaltask.dao.Dao} objects.
     * @param connectionManagerFactory Factory used to create {@link AbstractConnectionManager} subclasses
     * @param hashGeneratorFactory Factory used to generate [@link {@link HashGenerator}
     */
    public AuthenticationService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory,
                                 HashGeneratorFactory hashGeneratorFactory) {
        super(daoFactory, connectionManagerFactory);
        this.hashGeneratorFactory = hashGeneratorFactory;
    }

    /**
     * Creates AuthenticationService with {@link HashGeneratorFactoryImpl} and default {@link DaoFactory} and
     * {@link ConnectionManagerFactory} implementations defined in {@link AbstractService} no-arguments constructor.
     */
    public AuthenticationService() {
        super();
        this.hashGeneratorFactory = new HashGeneratorFactoryImpl();
    }

    /**
     * Checks chosen email and password are registered in application. Returns authenticated account if account
     * with chosen email and password hash exists
     * @param email Email used for authentication
     * @param password Password used for authentication
     * @return {@link Account} if authentication went successfully, else returns null
     * @throws ServiceException if PersistenceException is thrown while working with database
     */
    public Account authenticate(String email, String password) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            Account account = accountDao.findAccountByEmail(email);

            if (account != null) {
                HashGenerator hashGenerator = hashGeneratorFactory.createHashGenerator();
                String passwordHash = hashGenerator.hash(password, account.getSalt(), ApplicationConstants.HASHING_ALGORITHM);
                if (account.getPasswordHash().equals(passwordHash)) {
                    logger.log(Level.INFO, "User with email=" + email + " is authenticated successfully");
                } else {
                    logger.log(Level.INFO, "Cannot authenticate user with email=" + email + ", password hashes do not match");
                    return null;
                }
            } else {
                logger.log(Level.INFO, "User with email " + email + ", does not exist in the database, cannot authenticate");
            }
            return account;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
