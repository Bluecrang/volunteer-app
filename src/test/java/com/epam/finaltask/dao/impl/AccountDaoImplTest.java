package com.epam.finaltask.dao.impl;

import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class AccountDaoImplTest {

    private static final String BEFORE_METHOD_ACCOUNT_LOGIN = "LOGIN";
    private static final String BEFORE_METHOD_ACCOUNT_MAIL = "mail@mail.com";
    AbstractConnectionManagerImpl connectionManager;
    AccountDaoImpl accountDao;
    Connection connection;
    Account account;

    @BeforeClass
    public void init() {
        connectionManager = mock(AbstractConnectionManagerImpl.class);
        try {
            connection = DatabaseTestUtil.initiateDatabaseAndGetConnection();
            when(connectionManager.getConnection()).thenReturn(connection);
            accountDao = new AccountDaoImpl(connectionManager);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeMethod
    public void initBeforeMethod() {
        account = new Account(BEFORE_METHOD_ACCOUNT_LOGIN, "PASS", BEFORE_METHOD_ACCOUNT_MAIL, AccessLevel.USER,
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
    public void findAccountByLoginTest() {
        try {
            accountDao.create(account);
            Account actual = accountDao.findAccountByLogin(BEFORE_METHOD_ACCOUNT_LOGIN);
            Assert.assertEquals(actual.getLogin(), BEFORE_METHOD_ACCOUNT_LOGIN);
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
            Account accountByLogin = accountDao.findAccountByLogin(BEFORE_METHOD_ACCOUNT_LOGIN);

            Account actual = accountDao.findEntityById(accountByLogin.getAccountId());

            Assert.assertEquals(actual.getLogin(), accountByLogin.getLogin());
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
            Account accountToUpdate = accountDao.findAccountByLogin(BEFORE_METHOD_ACCOUNT_LOGIN);
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

            Assert.assertEquals(actual.getLogin(), account.getLogin());
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

    @AfterClass
    public void cleanUp() {
        try {
            DatabaseTestUtil.deregisterDrivers();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
