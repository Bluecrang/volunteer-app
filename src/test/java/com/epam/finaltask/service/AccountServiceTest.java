package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import org.apache.commons.io.input.ReaderInputStream;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Part;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class AccountServiceTest {

    AccountService accountService;
    AccountDao accountDao;
    AbstractConnectionManager connectionManager;
    DaoFactory daoFactory;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ConnectionManagerFactory connectionManagerFactory = mock(ConnectionManagerFactory.class);

        connectionManager = mock(AbstractConnectionManager.class);
        try {
            when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        } catch (PersistenceException e) {
            throw new RuntimeException("Unexpected exception while performing setUp", e);
        }

        daoFactory = mock(DaoFactory.class);

        accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        accountService = new AccountService(daoFactory, connectionManagerFactory);
    }

    @Test
    public void addValueToRatingTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        long accountId = 2;
        String login2 = "login2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, login2, hash2,
                email2, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(anyObject())).thenReturn(1);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = accountService.addValueToRating(actingAccount, accountId, 2);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void addValueToRatingTestActingAccountNotAdmin() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        long accountId = 2;
        try {
            boolean result = accountService.addValueToRating(actingAccount, accountId, 2);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void updateAvatarTestValidParameters() {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("image.png");
        try {
            when(part.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            fail("Unexpected IOException", e);
        }
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(1)).thenReturn(account);
            when(accountDao.update(anyObject())).thenReturn(1);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = accountService.updateAvatar(account, part);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void changeAccountBlockStateTestValidParameters() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, login, hash,
                email, AccessLevel.ADMIN, 0, true, false, "salt", null);
        String login2 = "login2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(2, login2, hash2,
                email2, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(2)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(anyObject())).thenReturn(1);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = accountService.changeAccountBlockState(blockingAccount, 2, true);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void changeAccountBlockStateTestBlockingAccountNotAdmin() {
        String login = "login";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, login, hash,
                email, AccessLevel.USER, 0, true, false, "salt", null);
        String login2 = "login2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(2, login2, hash2,
                email2, AccessLevel.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(2)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(anyObject())).thenReturn(1);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = accountService.changeAccountBlockState(blockingAccount, 2, true);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }
}
