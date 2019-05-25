package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

public class RegistrationServiceTest {

    private static final String DEFAULT_HASH = "hash";
    private RegistrationService registrationService;
    @Mock
    private AccountDao accountDao;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private AbstractConnectionManager connectionManager;
    @Mock
    private HashGeneratorFactory hashGeneratorFactory;
    @Mock
    private ConnectionManagerFactory connectionManagerFactory;
    @Mock
    private DaoFactory daoFactory;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(hashGeneratorFactory.createHashGenerator()).thenReturn(hashGenerator);
        try {
            when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        } catch (PersistenceException e) {
            throw new RuntimeException("Unexpected exception while performing setUp", e);
        }

        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        registrationService = new RegistrationService(daoFactory, connectionManagerFactory, hashGeneratorFactory);
    }

    @DataProvider(name = "InvalidParametersProvider")
    public Object[][] provideNullParameters() {
        return new Object[][] {
                {null, "password", "email@mail.com"},
                {"login", null, "mail@mail.ru"},
                {"log", "pass", null},
                {null, null, "email@mail.com"},
                {null, "pass", null},
                {"username", null, null},
                {null, null, null},
        };
    }

    @Test(dataProvider = "InvalidParametersProvider")
    public void registerAccountTestNullParameters(String username, String password, String email) {
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(null);
            when(accountDao.findAccountByUsername(username)).thenReturn(null);

            RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

            Assert.assertEquals(result, RegistrationService.RegistrationResult.ARGUMENT_IS_NULL);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void registerAccountTestValidParametersAndEmailAndUsernameAreFree() {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(null);
            when(accountDao.findAccountByUsername(username)).thenReturn(null);
            when(accountDao.create(anyObject())).thenReturn(true);

            RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

            verify(accountDao).create(anyObject());
            Assert.assertEquals(result, RegistrationService.RegistrationResult.SUCCESS);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void registerAccountTestEmailAlreadyExists() {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        Account account = new Account(1, username, DEFAULT_HASH, email, AccountType.USER,
                0, false, "salt", null);
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(account);
            when(accountDao.findAccountByUsername(username)).thenReturn(null);

            RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

            verify(accountDao).findAccountByEmail(email);
            Assert.assertEquals(result, RegistrationService.RegistrationResult.EMAIL_EXISTS);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void registerAccountTestUsernameAlreadyExists() {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        Account account = new Account(1, username, DEFAULT_HASH, email, AccountType.USER,
                0, false, "salt", null);
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(null);
            when(accountDao.findAccountByUsername(username)).thenReturn(account);

            RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

            verify(accountDao).findAccountByUsername(username);
            Assert.assertEquals(result, RegistrationService.RegistrationResult.USERNAME_EXISTS);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void registerAccountTestCouldNotAddAccountToTheDatabase() {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(null);
            when(accountDao.findAccountByUsername(username)).thenReturn(null);
            when(accountDao.create(anyObject())).thenReturn(false);

            RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

            verify((accountDao)).create(anyObject());
            Assert.assertEquals(result, RegistrationService.RegistrationResult.CANNOT_CREATE_ACCOUNT_IN_DATABASE);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void registerAccountTestPersistenceExceptionThrown() throws ServiceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        try {
            when(accountDao.findAccountByEmail(email)).thenThrow(new PersistenceException());
            when(accountDao.findAccountByUsername(username)).thenReturn(null);
            when(accountDao.create(anyObject())).thenReturn(true);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        registrationService.registerAccount(username, password, email);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void registerAccountTestPersistenceExceptionThrownBeforeTransaction() throws ServiceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        try {
            doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException while arranging");
        }
        registrationService.registerAccount(username, password, email);
    }
}
