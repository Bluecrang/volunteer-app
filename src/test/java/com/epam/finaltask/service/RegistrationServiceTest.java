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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class RegistrationServiceTest {

    private static final String DEFAULT_HASH = "hash";
    RegistrationService registrationService;
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

        registrationService = new RegistrationService(daoFactory, connectionManagerFactory, hashGeneratorFactory);
    }

    @DataProvider(name = "InvalidParametersProvider")
    public Object[][] provideInvalidParameters() {
        return new Object[][] {
                {null, "password", "email@mail.com"},
                {"login", null, "mail@mail.ru"},
                {"log", "pass", null}
        };
    }

    @Test(dataProvider = "InvalidParametersProvider")
    public void registerUserTestValidParametersAndAccountDoesNotExist(String login, String password, String email) {
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(null);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = registrationService.registerUser(login, password, email);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void registerUserTestInvalidParameters() {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(null);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = registrationService.registerUser(login, password, email);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void registerUserTestAccountAlreadyExists() {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";
        Account account = new Account(1, login, DEFAULT_HASH, email, AccessLevel.USER,
                0, true, false, "salt", null);
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(account);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = registrationService.registerUser(login, password, email);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void registerUserTestPersistenceExceptionThrown() {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";
        Account account = new Account(1, login, DEFAULT_HASH, email, AccessLevel.USER,
                0, true, false, "salt", null);
        try {
            when(accountDao.findAccountByEmail(email)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = registrationService.registerUser(login, password, email);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }
}