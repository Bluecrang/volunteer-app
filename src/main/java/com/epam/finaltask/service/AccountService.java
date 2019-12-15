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

/**
 * Service class that provides methods to add and manipulate database accounts.
 */
public class AccountService extends AbstractService {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Creates AccountService object with specified {@link DaoFactory} and {@link ConnectionManagerFactory}. Uses default
     * DaoFactory or ConnectionManagerFactory implementation specified in {@link AbstractService}
     * if corresponding factory parameter is null.
     * @param daoFactory Factory used to create DAO objects
     * @param connectionManagerFactory Factory used to create {@link com.epam.finaltask.dao.impl.AbstractConnectionManager} subclasses
     */
    public AccountService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory) {
        super(daoFactory, connectionManagerFactory);
    }

    /**
     * Creates AccountService object with specified {@link DaoFactory}. Uses default
     * DaoFactory implementation specified in {@link AbstractService} if daoFactory parameter is null.
     * Creates new {@link ConnectionManagerFactoryImpl} to be used as ConnectionManagerFactory.
     * @param daoFactory Factory used to create DAO objects
     */
    public AccountService(DaoFactory daoFactory) {
        super(daoFactory);
        this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
    }

    /**
     * Creates AccountService with default {@link DaoFactory} and {@link ConnectionManagerFactory} implementations
     * specified in the {@link AbstractService}.
     */
    public AccountService() {
        super();
    }

    /**
     * Returns database account count.
     * @return account count
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    public int countAccounts() throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            return accountDao.findAccountCount();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Returns accounts from the specific rating page.
     * @param page Page number
     * @param numberOfAccountsPerPage Number of account displayed on one page
     * @return Account on the page
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    public List<Account> findRatingPageAccounts(int page, int numberOfAccountsPerPage) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            return accountDao.findPageAccountsSortByRating(page, numberOfAccountsPerPage);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Adds chosen value to accounts rating. Returns {@code true if rating was successfully added}.
     * @param accountId Id of the account which rating will be changed
     * @param value Rating value that will be added to the rating of the chosen account
     * @return {@code true} if rating was successfully added
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    public boolean addValueToRating(long accountId, int value) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            connectionManager.disableAutoCommit();
            try {
                Account account = findAccountById(accountId, connectionManager);
                if (account != null) {
                    if (account.getRating() + value >= 0) {
                        if (account.getRating() + value > 1_000_000) {
                            account.setRating(1_000_000);
                        } else {
                            account.setRating(account.getRating() + value);
                        }
                    } else {
                        account.setRating(0);
                    }
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
        return false;
    }

    /**
     * Changes account block state. Returns {@code true} if block state was successfully changed.
     * @param accountId Id of the account which block state will be changed
     * @param block New block state
     * @return {@code true} if account block state was successfully changed
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    public boolean changeAccountBlockState(long accountId, boolean block) throws ServiceException {
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
        return false;
    }

    /**
     * Changed account avatar to the specified avatar.
     * @param account Account which avatar should be updated
     * @param part {@link Part} that contains new avatar
     * @return {@code true} if avatar was successfully updated
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
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

    /**
     * Returns account with specified from the database. Returns null if account with chosen id does not exist.
     * @param accountId Id of the account to be searched for
     * @return {@link Account} instance if account is found, {@code null} if account is not found
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    public Account findAccountById(long accountId) throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            return findAccountById(accountId, connectionManager);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Returns account with specified from the database. Returns null if account with chosen id does not exist.
     * Uses existing {@link AbstractConnectionManager} to find account.
     * @param accountId Id of the account to be searched for
     * @param connectionManager {@link AbstractConnectionManager} subclass that provides database access
     * @return {@link Account} instance if account is found, {@code null} if account is not found
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    Account findAccountById(long accountId, AbstractConnectionManager connectionManager) throws ServiceException {
        AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
        try {
            return accountDao.findEntityById(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Changes account type of the account with chosen id to the chosen account type.
     * @param accountId Id of the account which type will be changed
     * @param accountType Type of the account to set
     * @return {@code true} if account type was set successfully, else returns {@code false}
     * @throws ServiceException If PersistenceException is thrown while working with database
     */
    public boolean changeAccountType(long accountId, AccountType accountType) throws ServiceException {
        if (accountType != null && accountType != AccountType.GUEST) {
            try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
                connectionManager.disableAutoCommit();
                Account account = findAccountById(accountId, connectionManager);
                if (account != null) {
                    logger.log(Level.DEBUG, "account found: id=" + account.getAccountId());
                    account.setAccountType(accountType);
                    AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
                    int result = accountDao.update(account);
                    connectionManager.commit();
                    return (result == 1);
                }
                connectionManager.rollback();
                logger.log(Level.WARN, "could not change account type: account not found in the database");
            } catch (PersistenceException e) {
                throw new ServiceException(e);
            }
        }
        return false;
    }

    /**
     * Returns list of all account which {@link AccountType} is {@link AccountType#ADMIN}.
     * @return List of administrators accounts.
     * @throws ServiceException if {@link PersistenceException} has occurred when working with database
     */
    public List<Account> findAdministrators() throws ServiceException {
        try (AbstractConnectionManager connectionManager = connectionManagerFactory.createConnectionManager()) {
            AccountDao accountDao = daoFactory.createAccountDao(connectionManager);
            return accountDao.findAllByAccountType(AccountType.ADMIN);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
}
