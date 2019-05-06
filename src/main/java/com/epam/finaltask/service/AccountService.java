package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.impl.AccountDaoImpl;
import com.epam.finaltask.dao.impl.ConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.validation.ImageFilenameValidator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AccountService {

    private static final Logger logger = LogManager.getLogger();

    public boolean addValueToRating(Account actingAccount, long accountId, int value) throws ServiceException {
        if (actingAccount != null && actingAccount.getAccessLevel() != null &&
                actingAccount.getAccessLevel().equals(AccessLevel.ADMIN)) {
            try (ConnectionManager connectionManager = new ConnectionManager()) {
                connectionManager.disableAutoCommit();
                try {
                    Account account = findAccountById(accountId, connectionManager);
                    if (account != null) {
                        account.setRating(account.getRating() + value);
                        AccountDao accountDao = new AccountDaoImpl(connectionManager);
                        if (accountDao.update(account) == 1) {
                            connectionManager.commit();
                            return true;
                        }
                    }
                    connectionManager.rollback();
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

    public List<Account> findAllAccounts() throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            AccountDao accountDao = new AccountDaoImpl(connectionManager);
            return accountDao.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean changeAccountBlockState(Account blockingAccount, long accountId, boolean blocked) throws ServiceException {
        if (blockingAccount != null && blockingAccount.getAccessLevel() != null &&
                blockingAccount.getAccessLevel().equals(AccessLevel.ADMIN)) {
            try (ConnectionManager connectionManager = new ConnectionManager()) {
                connectionManager.disableAutoCommit();
                try {
                    Account account = findAccountById(accountId, connectionManager);
                    if (account != null) {
                        account.setBlocked(blocked);
                        AccountDao accountDao = new AccountDaoImpl(connectionManager);
                        if (accountDao.update(account) == 1) {
                            connectionManager.commit();
                            return true;
                        }
                    }
                    connectionManager.rollback();
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

    public Account findAccountById(long accountId) throws ServiceException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            return findAccountById(accountId, connectionManager);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean updateAvatar(Account account, Part part) throws ServiceException {
        if (account == null) {
            logger.log(Level.WARN, "cannot update avatar: account is null");
            return false;
        }
        if (part == null) {
            logger.log(Level.WARN, "cannot update avatar: part is null");
            return false;
        }
        ImageFilenameValidator imageFilenameValidator = new ImageFilenameValidator();
        if (!imageFilenameValidator.validate(part.getSubmittedFileName())) {
            logger.log(Level.WARN, "cannot update avatar: file has wrong extension");
            return false;
        }
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            AccountDao accountDao = new AccountDaoImpl(connectionManager);
            Account accountFromDatabase = accountDao.findEntityById(account.getAccountId());
            try (InputStream inputStream = part.getInputStream()){
                accountFromDatabase.setAvatarBase64(Base64.encodeBase64String(IOUtils.toByteArray(inputStream)));
                return accountDao.update(accountFromDatabase) == 1;
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (IOException e) {
            throw new ServiceException("IOException while updating avatar", e);
        }
    }

    Account findAccountById(long accountId, ConnectionManager connectionManager) throws ServiceException {
        AccountDaoImpl accountDao = new AccountDaoImpl(connectionManager);
        try {
            return accountDao.findEntityById(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
