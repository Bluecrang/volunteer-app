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
    public void setUp() throws PersistenceException {
        MockitoAnnotations.initMocks(this);
        when(hashGeneratorFactory.createHashGenerator()).thenReturn(hashGenerator);
        when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);
        authenticationService = new AuthenticationService(daoFactory, connectionManagerFactory, hashGeneratorFactory);
    }

    @Test
    public void authenticate_accountExistsPasswordValid_notNull() throws ServiceException, PersistenceException {
        String email = "email";
        String password = "password";
        when(hashGenerator.hash(eq(password), eq(DEFAULT_SALT), anyString())).thenReturn(DEFAULT_HASH);
        when(accountDao.findAccountByEmail(email)).thenReturn(new Account(1, email, DEFAULT_HASH,
                "email@mail.com", AccountType.USER, 0, false, DEFAULT_SALT, null));

        Account result = authenticationService.authenticate(email, password);

        Assert.assertNotNull(result);
        verify(accountDao).findAccountByEmail(email);
        verify(hashGenerator).hash(eq(password), eq(DEFAULT_SALT), anyString());
    }

    @Test
    public void authenticate_emailNull_AuthenticationException() throws PersistenceException {
        String email = null;
        String password = "password";
        when(accountDao.findAccountByEmail(email)).thenReturn(null);

        Assert.assertThrows(AuthenticationException.class, () -> {
            authenticationService.authenticate(email, password);
        });
        verify(accountDao).findAccountByEmail(email);
    }

    @Test
    public void authenticate_userDoesNotExistInDatabase_AuthenticationException() throws PersistenceException {
        String email = "email";
        String password = "password";
        when(accountDao.findAccountByEmail(email)).thenReturn(null);

        Assert.assertThrows(AuthenticationException.class, () -> {
            authenticationService.authenticate(email, password);
        });
        verify(accountDao).findAccountByEmail(email);
    }

    @Test
    public void authenticate_passwordHashesDoNotMatch_AuthenticationException() throws PersistenceException {
        String email = "email";
        String password = "password";
        when(hashGenerator.hash(eq(password), eq(DEFAULT_SALT), anyString())).thenReturn("other_hash");
        when(accountDao.findAccountByEmail(email)).thenReturn(new Account(1, email, DEFAULT_HASH,
                "email@mail.com", AccountType.USER, 0, false, DEFAULT_SALT, null));

        Assert.assertThrows(AuthenticationException.class, () -> {
            authenticationService.authenticate(email, password);
        });
        verify(accountDao).findAccountByEmail(email);
        verify(hashGenerator).hash(eq(password), eq(DEFAULT_SALT), anyString());
    }

    @Test
    public void authenticate_PersistenceExceptionThrown_ServiceException() throws PersistenceException {
        String email = "email";
        String password = "password";
        when(accountDao.findAccountByEmail(email)).thenThrow(new PersistenceException());

        Assert.assertThrows(ServiceException.class, () -> {
            authenticationService.authenticate(email, password);
        });
        verify(accountDao).findAccountByEmail(email);
    }
}
