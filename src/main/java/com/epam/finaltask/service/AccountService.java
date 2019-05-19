package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.ConnectionManagerFactoryImpl;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
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

public class AccountService extends AbstractService {

    public AccountService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory) {
        super(daoFactory, connectionManagerFactory);
    }

    public AccountService(DaoFactory daoFactory) {
        super(daoFactory);
        this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
    }

    public AccountService() {
        super();
    }

    private static final Logger logger = LogManager.getLogger();

    public int countAccounts() throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            return accountDao.findAccountCount();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<Account> findRatingPageAccounts(int page, int numberOfAccountsPerPage) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            return accountDao.findPageAccountsSortByRating(page, numberOfAccountsPerPage);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean addValueToRating(Account actingAccount, long accountId, int value) throws ServiceException {
        if (actingAccount != null && actingAccount.getAccountType() != null &&
                actingAccount.getAccountType().equals(AccountType.ADMIN)) {
            try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
                connectionManager.disableAutoCommit();
                try {
                    Account account = findAccountById(accountId, connectionManager);
                    if (account != null) {
                        account.setRating(account.getRating() + value);
                        AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
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

    public boolean changeAccountBlockState(Account blockingAccount, long accountId, boolean block) throws ServiceException {
        if (blockingAccount != null && blockingAccount.getAccountType() != null &&
                blockingAccount.getAccountType().equals(AccountType.ADMIN)) {
            try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
                connectionManager.disableAutoCommit();
                try {
                    Account account = findAccountById(accountId, connectionManager);
                    if (account != null) {
                        account.setBlocked(block);
                        AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
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
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
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

    public Account findAccountById(long accountId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            return findAccountById(accountId, connectionManager);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    Account findAccountById(long accountId, AbstractConnectionManager connectionManager) throws ServiceException {
        AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
        try {
            return accountDao.findEntityById(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean promoteUserToAdmin(Account sessionAccount, long accountId) throws ServiceException {
        if (sessionAccount == null) {
            logger.log(Level.WARN, "cannot promote account: sessionAccount is null");
            return false;
        }
        if (!AccountType.ADMIN.equals(sessionAccount.getAccountType())) {
            logger.log(Level.WARN, "cannot promote account: sessionAccount access level is not admin");
            return false;
        }
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            Account account = findAccountById(accountId, connectionManager);
            if (account != null) {
                account.setAccountType(AccountType.ADMIN);
                AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                int result = accountDao.update(account);
                connectionManager.commit();
                return (result == 1);
            }
            connectionManager.rollback();
            logger.log(Level.WARN, "could not promote account: account not found in the database");
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return false;
    }

    public List<Account> findAdministrators() throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            return accountDao.findAllByAccountType(AccountType.ADMIN);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
