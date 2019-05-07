package com.epam.finaltask.dao.impl;

import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class AccountDaoImplTest {

    ConnectionManager connectionManager;
    AccountDaoImpl accountDao;
    Connection connection;
    Account account;

    @BeforeClass
    public void init() {
        connectionManager = mock(ConnectionManager.class);
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
        account = new Account("LOGIN", "PASS", "mail@mail.com", AccessLevel.USER,
        0, true, false, "salt", null);
    }

    @Test
    public void findAccountByLoginTest() {
        try {
            accountDao.create(account);
            Account actual = accountDao.findAccountByLogin("LOGIN");
            Assert.assertEquals(actual.getLogin(), "LOGIN");
            accountDao.delete(actual.getAccountId());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAllTest() {
        try {
            accountDao.create(account);
            List<Account> actual = accountDao.findAll();
            for (Account databaseAccount : actual) {
                if (databaseAccount.getAccountId() != 1) {
                    accountDao.delete(databaseAccount.getAccountId());
                }
            }
            Assert.assertEquals(actual.size(), 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findEntityByIdTest() {
        try {
            accountDao.create(account);
            Account accountByLogin = accountDao.findAccountByLogin("LOGIN");

            Account actual = accountDao.findEntityById(accountByLogin.getAccountId());
            accountDao.delete(accountByLogin.getAccountId());

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

            Account accountByLogin = accountDao.findAccountByLogin("LOGIN");
            accountDao.delete(accountByLogin.getAccountId());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void updateTest() {
        try {
            accountDao.create(account);
            Account accountToUpdate = accountDao.findAccountByLogin("LOGIN");
            accountToUpdate.setPasswordHash("changed");

            accountDao.update(accountToUpdate);

            Account actual = accountDao.findEntityById(accountToUpdate.getAccountId());
            accountDao.delete(actual.getAccountId());

            Assert.assertEquals(actual.getPasswordHash(), "changed");
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findAccountByEmailTest() {
        try {
            accountDao.create(account);
            Account actual = accountDao.findAccountByEmail("mail@mail.com");

            accountDao.delete(actual.getAccountId());
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
            for (Account databaseAccount : actual) {
                if (databaseAccount.getAccountId() != 1) {
                    accountDao.delete(databaseAccount.getAccountId());
                }
            }
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
