package com.epam.finaltask.service;

import com.epam.finaltask.dao.impl.AccountDaoImpl;
import com.epam.finaltask.dao.impl.ConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.util.ApplicationConstants;
import com.epam.finaltask.util.HashGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger();

    public Account authenticate(String login, String password) throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            AccountDaoImpl accountDao = new AccountDaoImpl(connectionManager);
            Account account = accountDao.findAccountByLogin(login);

            if (account != null) {
                HashGenerator hashGenerator = new HashGenerator();
                String passwordHash = hashGenerator.hash(password, account.getSalt(), ApplicationConstants.HASHING_ALGORITHM);
                if (account.getPasswordHash().equals(passwordHash)) {
                    logger.log(Level.INFO, "User with login=" + login + " is authenticated successfully");
                } else {
                    logger.log(Level.INFO, "Cannot authenticate user with login=" + login + ", password hashes do not match");
                    return null;
                }
            } else {
                logger.log(Level.INFO, "User with login +" + login + ", does not exist in the database, cannot authenticate");
            }
            return account;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
