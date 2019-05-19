package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.entity.AccessLevel;
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
            DatabaseTestUtil.registerDrivers();
            DatabaseTestUtil.initializeDatabase();
            Connection connection = DatabaseTestUtil.getConnection();
            MockitoAnnotations.initMocks(this);
            when(connectionManager.getConnection()).thenReturn(connection);
            accountDao = new AccountDaoImpl(connectionManager);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeMethod
    public void initBeforeMethod() {
        account = new Account(BEFORE_METHOD_ACCOUNT_USERNAME, "PASS", BEFORE_METHOD_ACCOUNT_MAIL, AccessLevel.USER,
        0, true, false, "salt", null);
    }

    @AfterMethod
    public void cleanUpDatabaseAccounts() {
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
    public void findAccountByUsernameTest() {
        try {
            accountDao.create(account);
            Account actual = accountDao.findAccountByUsername(BEFORE_METHOD_ACCOUNT_USERNAME);
            Assert.assertEquals(actual.getUsername(), BEFORE_METHOD_ACCOUNT_USERNAME);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAllTest() {
        try {
            accountDao.create(account);
            List<Account> actual = accountDao.findAll();
            Assert.assertEquals(actual.size(), 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAccountCountTest() {
        try {
            accountDao.create(account);
            int actual = accountDao.findAccountCount();
            Assert.assertEquals(actual, 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findEntityByIdTest() {
        try {
            accountDao.create(account);
            Account accountByLogin = accountDao.findAccountByUsername(BEFORE_METHOD_ACCOUNT_USERNAME);

            Account actual = accountDao.findEntityById(accountByLogin.getAccountId());

            Assert.assertEquals(actual.getUsername(), accountByLogin.getUsername());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void createTest() {
        try {
            boolean result = accountDao.create(account);

            Assert.assertTrue(result);

        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void updateTest() {
        try {
            accountDao.create(account);
            Account accountToUpdate = accountDao.findAccountByUsername(BEFORE_METHOD_ACCOUNT_USERNAME);
            accountToUpdate.setPasswordHash("changed");

            int actual = accountDao.update(accountToUpdate);

            Assert.assertEquals(actual, 1);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAccountByEmailTest() {
        try {
            accountDao.create(account);

            Account actual = accountDao.findAccountByEmail(BEFORE_METHOD_ACCOUNT_MAIL);

            Assert.assertEquals(actual.getUsername(), account.getUsername());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findPageAccountsSortByRatingTest() {
        try {
            account.setRating(3);
            accountDao.create(account);

            List<Account> actual = accountDao.findPageAccountsSortByRating(1, 2);

            Assert.assertEquals(actual.get(1).getRating(), 3);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
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
}
