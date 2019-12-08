package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

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
    public void setUp() throws PersistenceException {
        MockitoAnnotations.initMocks(this);
        when(hashGeneratorFactory.createHashGenerator()).thenReturn(hashGenerator);
        when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);

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
    public void registerAccount_nullParameters_registrationResultArgumenNull(String username, String password, String email)
            throws PersistenceException, ServiceException {
        when(accountDao.findAccountByEmail(email)).thenReturn(null);
        when(accountDao.findAccountByUsername(username)).thenReturn(null);

        RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

        Assert.assertEquals(result, RegistrationService.RegistrationResult.ARGUMENT_IS_NULL);
    }

    @Test
    public void registerAccount_validParametersAndEmailAndUsernameAreFree_success()throws PersistenceException, ServiceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        when(accountDao.findAccountByEmail(email)).thenReturn(null);
        when(accountDao.findAccountByUsername(username)).thenReturn(null);
        when(accountDao.create(anyObject())).thenReturn(true);

        RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

        verify(accountDao).findAccountByEmail(email);
        verify(accountDao).findAccountByUsername(username);
        verify(accountDao).create(anyObject());
        Assert.assertEquals(result, RegistrationService.RegistrationResult.SUCCESS);
    }

    @Test
    public void registerAccount_emailAlreadyExists_emailExists() throws PersistenceException, ServiceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        Account account = new Account(1, username, DEFAULT_HASH, email, AccountType.USER,
                0, false, "salt", null);
        when(accountDao.findAccountByEmail(email)).thenReturn(account);
        when(accountDao.findAccountByUsername(username)).thenReturn(null);

        RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

        verify(accountDao).findAccountByEmail(email);
        Assert.assertEquals(result, RegistrationService.RegistrationResult.EMAIL_EXISTS);
    }

    @Test
    public void registerAccount_usernameAlreadyExists_usernameExists() throws PersistenceException, ServiceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        Account account = new Account(1, username, DEFAULT_HASH, email, AccountType.USER,
                0, false, "salt", null);
        when(accountDao.findAccountByEmail(email)).thenReturn(null);
        when(accountDao.findAccountByUsername(username)).thenReturn(account);

        RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

        verify(accountDao).findAccountByUsername(username);
        Assert.assertEquals(result, RegistrationService.RegistrationResult.USERNAME_EXISTS);
    }

    @Test
    public void registerAccount_couldNotAddAccountToTheDatabase_databaseErrorRegistrationResult()
            throws PersistenceException, ServiceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        when(accountDao.findAccountByEmail(email)).thenReturn(null);
        when(accountDao.findAccountByUsername(username)).thenReturn(null);
        when(accountDao.create(anyObject())).thenReturn(false);

        RegistrationService.RegistrationResult result = registrationService.registerAccount(username, password, email);

        verify(accountDao).findAccountByEmail(email);
        verify(accountDao).findAccountByUsername(username);
        verify((accountDao)).create(anyObject());
        Assert.assertEquals(result, RegistrationService.RegistrationResult.CANNOT_CREATE_ACCOUNT_IN_DATABASE);
    }

    @Test
    public void registerAccount_persistenceExceptionThrown_serviceException() throws PersistenceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        when(accountDao.findAccountByEmail(email)).thenThrow(new PersistenceException());
        when(accountDao.findAccountByUsername(username)).thenReturn(null);

        Assert.assertThrows(ServiceException.class, () -> {
            registrationService.registerAccount(username, password, email);
        });
        verify(accountDao).findAccountByEmail(email);
    }

    @Test
    public void registerAccount_persistenceExceptionThrownDisableAutoCommit_serviceException() throws PersistenceException {
        String username = "username";
        String password = "password";
        String email = "mail@mail.com";
        doThrow(new PersistenceException()).when(connectionManager).disableAutoCommit();

        Assert.assertThrows(ServiceException.class, () -> {
            registrationService.registerAccount(username, password, email);
        });
        verify(connectionManager).disableAutoCommit();
    }
}
