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
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

public class AuthenticationServiceTest {

    private static final String DEFAULT_HASH = "hash";
    private static final String DEFAULT_SALT = "salt";
    private AuthenticationService authenticationService;
    @Mock
    private AccountDao accountDao;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashGeneratorFactory hashGeneratorFactory;
    @Mock
    ConnectionManagerFactory connectionManagerFactory;
    @Mock
    AbstractConnectionManager connectionManager;
    @Mock
    DaoFactory daoFactory;

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
        authenticationService = new AuthenticationService(daoFactory, connectionManagerFactory, hashGeneratorFactory);
    }

    @Test
    public void authenticateTestValidUsername() {
        String email = "email";
        String password = "password";
        when(hashGenerator.hash(eq(password), eq(DEFAULT_SALT), anyString())).thenReturn(DEFAULT_HASH);
        try {
            when(accountDao.findAccountByEmail(email)).thenReturn(new Account(1, email, DEFAULT_HASH,
                    "email@mail.com", AccountType.USER, 0, false, DEFAULT_SALT, null));
            Account result = authenticationService.authenticate(email, password);

            verify(accountDao).findAccountByEmail(email);
            Assert.assertNotNull(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = AuthenticationException.class)
    public void authenticateTestEmailNull() throws PersistenceException, ServiceException {
        String email = null;
        String password = "password";
        when(accountDao.findAccountByEmail(email)).thenReturn(null);

        authenticationService.authenticate(email, password);
    }

    @Test(expectedExceptions = AuthenticationException.class)
    public void authenticateTestUserDoesNotExistInDatabase() throws PersistenceException, ServiceException {
        String email = "email";
        String password = "password";
        when(accountDao.findAccountByEmail(email)).thenReturn(null);

        authenticationService.authenticate(email, password);
    }

    @Test(expectedExceptions = AuthenticationException.class)
    public void authenticateTestPasswordHashesDoNotMatch() throws PersistenceException, ServiceException {
        String email = "email";
        String password = "password";
        when(hashGenerator.hash(eq(password), eq(DEFAULT_SALT), anyString())).thenReturn("other_hash");
        when(accountDao.findAccountByEmail(email)).thenReturn(new Account(1, email, DEFAULT_HASH,
                "email@mail.com", AccountType.USER, 0, false, DEFAULT_SALT, null));

        authenticationService.authenticate(email, password);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void authenticateTestPersistenceExceptionThrown() throws ServiceException {
        String email = "email";
        String password = "password";
        try {
            when(accountDao.findAccountByEmail(email)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        authenticationService.authenticate(email, password);
    }
}
