package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class AuthenticationServiceTest {

    private static final String DEFAULT_HASH = "hash";
    AuthenticationService authenticationService;
    AccountDao accountDao;
    HashGenerator hashGenerator;

    @BeforeMethod
    public void setUp() {

        HashGeneratorFactory hashGeneratorFactory = mock(HashGeneratorFactory.class);

        hashGenerator = mock(HashGenerator.class);
        when(hashGeneratorFactory.createHashGenerator()).thenReturn(hashGenerator);

        ConnectionManagerFactory connectionManagerFactory = mock(ConnectionManagerFactory.class);

        AbstractConnectionManager connectionManager = mock(AbstractConnectionManager.class);
        try {
            when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        } catch (PersistenceException e) {
            throw new RuntimeException("Unexpected exception while performing setUp", e);
        }

        DaoFactory daoFactory = mock(DaoFactory.class);

        accountDao = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        authenticationService = new AuthenticationService(daoFactory, connectionManagerFactory, hashGeneratorFactory);
    }

    @Test
    public void authenticateTestValidLogin() {
        String login = "login";
        String password = "password";
        when(hashGenerator.hash(eq(password), anyString(), anyString())).thenReturn(DEFAULT_HASH);
        try {
            when(accountDao.findAccountByLogin(login)).thenReturn(new Account(1, login, DEFAULT_HASH,
                    "email@mail.com", AccessLevel.USER, 0, true, false, "salt", null));
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            Account result = authenticationService.authenticate(login, password);

            Assert.assertNotNull(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void authenticateTestLoginNull() {
        String login = null;
        String password = "password";
        when(hashGenerator.hash(eq(login), eq(password), anyString())).thenReturn(DEFAULT_HASH);
        try {
            when(accountDao.findAccountByLogin(login)).thenReturn(null);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            Account result = authenticationService.authenticate(login, password);

            Assert.assertNull(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void authenticateTestUserDoesNotExistInDatabase() {
        String login = "login";
        String password = "password";
        when(hashGenerator.hash(eq(login), eq(password), anyString())).thenReturn(DEFAULT_HASH);
        try {
            when(accountDao.findAccountByLogin(login)).thenReturn(null);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            Account result = authenticationService.authenticate(login, password);

            Assert.assertNull(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void authenticateTestPasswordHashesDoNotMatch() {
        String login = "login";
        String password = "password";
        when(hashGenerator.hash(eq(login), eq(password), anyString())).thenReturn("other_hash");
        try {
            when(accountDao.findAccountByLogin(login)).thenReturn(null);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            Account result = authenticationService.authenticate(login, password);

            Assert.assertNull(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void authenticateTestPersistenceExceptionThrown() throws ServiceException {
        String login = "login";
        String password = "password";
        when(hashGenerator.hash(eq(login), eq(password), anyString())).thenReturn(DEFAULT_HASH);
        try {
            when(accountDao.findAccountByLogin(login)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        authenticationService.authenticate(login, password);
    }
}
