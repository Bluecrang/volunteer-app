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

public class AuthenticationService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();
    private HashGeneratorFactory hashGeneratorFactory;

    public AuthenticationService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory,
                                 HashGeneratorFactory hashGeneratorFactory) {
        super(daoFactory, connectionManagerFactory);
        this.hashGeneratorFactory = hashGeneratorFactory;
    }

    public AuthenticationService() {
        super();
        this.hashGeneratorFactory = new HashGeneratorFactoryImpl();
    }

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
                logger.log(Level.INFO, "User with email +" + email + ", does not exist in the database, cannot authenticate");
            }
            return account;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
