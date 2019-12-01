package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Account;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class AccountDaoImplTest {

    private static final String BEFORE_METHOD_ACCOUNT_USERNAME = "USERNAME";
    private static final String BEFORE_METHOD_ACCOUNT_MAIL = "mail@mail.com";
    private AccountDao accountDao;
    private Account account;
    @Mock
    private AbstractConnectionManager connectionManager;

    @BeforeClass
    public void init() {
        try {
            MockitoAnnotations.initMocks(this);
            DatabaseTestUtil.registerDrivers();
            DatabaseTestUtil.initializeDatabase();
            Connection connection = DatabaseTestUtil.getConnection();
            when(connectionManager.getConnection()).thenReturn(connection);
            accountDao = new AccountDaoImpl(connectionManager);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        try {
            DatabaseTestUtil.dropSchema();
            DatabaseTestUtil.deregisterDrivers();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeMethod
    public void initBeforeMethod() {
        account = new Account(BEFORE_METHOD_ACCOUNT_USERNAME, "PASS", BEFORE_METHOD_ACCOUNT_MAIL, AccountType.USER,
        0, false, "salt", null);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanDatabaseAccountsTable() {
        try {
            List<Account> actual = accountDao.findAll();
            for (Account databaseAccount : actual) {
                if (databaseAccount.getAccountId() != 1) {
                    accountDao.delete(databaseAccount.getAccountId());
                }
            }
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findAccountByUsername_accountExist_accountFound() throws PersistenceException {
        accountDao.create(account);

        Account actual = accountDao.findAccountByUsername(BEFORE_METHOD_ACCOUNT_USERNAME);

        Assert.assertEquals(actual.getUsername(), BEFORE_METHOD_ACCOUNT_USERNAME);
    }

    @Test
    public void findAll_twoAccountsExist_twoAccounts() throws PersistenceException {
        accountDao.create(account);

        List<Account> actual = accountDao.findAll();

        Assert.assertEquals(actual.size(), 2);
    }

    @Test
    public void findAllByAccountType_noAccountsWithChosenAccountTypeExistAccountTypeAdmin_noAccountsFound()
            throws PersistenceException {
        accountDao.create(account);

        List<Account> actual = accountDao.findAllByAccountType(AccountType.ADMIN);

        Assert.assertEquals(actual.size(), 0);
    }

    @Test
    public void findAllByAccountType_AaccountWithChosenAccountTypeExistsAccountTypeAdmin_oneAccountFound()
            throws PersistenceException {
        accountDao.create(account);
        Account accountAdmin = new Account("name3", "PAS", "mail@ma.ru", AccountType.ADMIN,
                10, false, "s", null);
        accountDao.create(accountAdmin);

        List<Account> actual = accountDao.findAllByAccountType(AccountType.ADMIN);

        Assert.assertEquals(actual.size(), 1);
    }

    @Test
    public void findAllByAccountType_accountTypeUser_twoAccountsFound() throws PersistenceException {
        accountDao.create(account);

        List<Account> actual = accountDao.findAllByAccountType(AccountType.USER);

        Assert.assertEquals(actual.size(), 2);
    }

    @Test
    public void findAccountCount_twoAccountsExist_two() throws PersistenceException {
        int expected = 2;
        accountDao.create(account);

        int actual = accountDao.findAccountCount();

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void findEntityById_entityExist_entityFound() throws PersistenceException {
        accountDao.create(account);
        Account accountByLogin = accountDao.findAccountByUsername(BEFORE_METHOD_ACCOUNT_USERNAME);

        Account actual = accountDao.findEntityById(accountByLogin.getAccountId());

        Assert.assertEquals(actual.getUsername(), accountByLogin.getUsername());
    }

    @Test
    public void findEntityById_entityDoesNotExist_null() throws PersistenceException {
        long id = 155;

        Account actual = accountDao.findEntityById(id);

        Assert.assertNull(actual);
    }

    @Test
    public void create_validAccount_true() throws PersistenceException {
        boolean result = accountDao.create(account);

        Assert.assertTrue(result);
    }

    @Test
    public void update_validUpdateData_success() throws PersistenceException {
        int expected = 1;
        accountDao.create(account);
        Account accountToUpdate = accountDao.findAccountByUsername(BEFORE_METHOD_ACCOUNT_USERNAME);
        accountToUpdate.setPasswordHash("changed");

        int actual = accountDao.update(accountToUpdate);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void update_accountDoesNotExist_success() throws PersistenceException {
        int expected = 0;
        account.setAccountId(164);

        int actual = accountDao.update(account);

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void findAccountByEmail_accountExists_accountFound() throws PersistenceException {
        accountDao.create(account);

        Account actual = accountDao.findAccountByEmail(BEFORE_METHOD_ACCOUNT_MAIL);

        Assert.assertEquals(actual.getUsername(), account.getUsername());
    }

    @Test
    public void findPageAccountsSortByRating_twoAccounts_sortedList() throws PersistenceException {
        account.setRating(3);
        accountDao.create(account);

        List<Account> actual = accountDao.findPageAccountsSortByRating(1, 2);

        Assert.assertTrue(actual.get(0).getRating() > actual.get(1).getRating());
    }
}
