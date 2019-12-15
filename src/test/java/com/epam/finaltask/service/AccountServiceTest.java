package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.fail;

public class AccountServiceTest {

    private AccountService accountService;
    @Mock
    private AccountDao accountDao;
    @Mock
    private AbstractConnectionManager connectionManager;
    @Mock
    private DaoFactory daoFactory;
    @Mock
    private ConnectionManagerFactory connectionManagerFactory;
    

    @BeforeMethod
    public void setUp() throws PersistenceException {
        MockitoAnnotations.initMocks(this);
        when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        accountService = new AccountService(daoFactory, connectionManagerFactory);
    }

    @Test
    public void addValueToRating_validParameters_true() throws PersistenceException, ServiceException {
        long accountId = 2;
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, username2, hash2,
                email2, AccountType.USER, 0, false, "salt", null);
        when(accountDao.findEntityById(accountId)).thenReturn(account);
        AccountDao updater = mock(AccountDao.class);
        when(updater.update(account)).thenReturn(1);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

        boolean result = accountService.addValueToRating(accountId, 2);

        verify(accountDao).findEntityById(accountId);
        Assert.assertTrue(result);
        Assert.assertEquals(account.getRating(), 2);
    }

    @Test
    public void addValueToRating_ratingAndValueSumLessThanZero_trueAndRatingZero() throws PersistenceException, ServiceException {
        long accountId = 2;
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, username2, hash2,
                email2, AccountType.USER, 0, false, "salt", null);
        when(accountDao.findEntityById(accountId)).thenReturn(account);
        AccountDao updater = mock(AccountDao.class);
        when(updater.update(account)).thenReturn(1);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

        boolean result = accountService.addValueToRating(accountId, -2);

        verify(accountDao).findEntityById(accountId);
        Assert.assertTrue(result);
        Assert.assertEquals(account.getRating(), 0);
    }

    @Test
    public void addValueToRating_ratingAndValueSumGreaterThanMillion_trueAndRatingMillion()
            throws PersistenceException, ServiceException {
        long accountId = 2;
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, username2, hash2,
                email2, AccountType.USER, 0, false, "salt", null);
        when(accountDao.findEntityById(accountId)).thenReturn(account);
        AccountDao updater = mock(AccountDao.class);
        when(updater.update(account)).thenReturn(1);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

        boolean result = accountService.addValueToRating(accountId, 1_000_000_0);

        verify(accountDao).findEntityById(accountId);
        Assert.assertTrue(result);
        Assert.assertEquals(account.getRating(), 1_000_000);
    }

    @Test
    public void addValueToRating_foundAccountNull_false() throws PersistenceException, ServiceException {
        long accountId = 2;
        when(accountDao.findEntityById(accountId)).thenReturn(null);

        boolean result = accountService.addValueToRating(accountId, 2);

        verify(accountDao).findEntityById(accountId);
        Assert.assertFalse(result);
    }

    @Test
    public void addValueToRating_persistenceExceptionThrownWhenFindingById_serviceException() throws PersistenceException {
        long accountId = 2;
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(accountId)).thenThrow(new PersistenceException());

        assertThrows(ServiceException.class, () -> {
            accountService.addValueToRating(accountId, 2);
        });
    }

    @Test
    public void addValueToRating_persistenceExceptionThrownWhenUpdating_serviceException() throws PersistenceException {
        long accountId = 2;
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, username2, hash2,
                email2, AccountType.USER, 0, false, "salt", null);
        AccountDao accountDao2 = mock(AccountDao.class);
        when(accountDao2.findEntityById(accountId)).thenReturn(account);
        AccountDao updater = mock(AccountDao.class);
        when(updater.update(account)).thenThrow(new PersistenceException());
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, updater);

        assertThrows(ServiceException.class, () -> {
            accountService.addValueToRating(accountId, 2);
        });
    }

    @Test
    public void addValueToRating_persistenceExceptionThrownWhileCreatingConnectionManager_serviceException()
            throws PersistenceException {
        long accountId = 2;
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        assertThrows(ServiceException.class, () -> {
            accountService.addValueToRating(accountId, 2);
        });
    }

    @Test
    public void updateAvatar_validParameters_true() throws PersistenceException, ServiceException {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("image.png");
        try {
            when(part.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            fail("Unexpected IOException", e);
        }
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);
        when(accountDao.findEntityById(1)).thenReturn(account);
        when(accountDao.update(account)).thenReturn(1);

        boolean result = accountService.updateAvatar(account, part);

        verify(accountDao).update(account);
        Assert.assertTrue(result);
    }

    @Test
    public void updateAvatar_IOExceptionThrownWhileGettingStream_serviceException() throws PersistenceException, IOException {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("image.png");
        when(part.getInputStream()).thenThrow(new IOException());
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);
        when(accountDao.findEntityById(1)).thenReturn(account);
        when(accountDao.update(account)).thenReturn(1);

        assertThrows(ServiceException.class, () -> {
            accountService.updateAvatar(account, part);
        });
    }

    @Test
    public void updateAvatar_persistenceExceptionThrown_serviceException() throws PersistenceException, IOException {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("image.png");
        when(part.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(1)).thenThrow(new PersistenceException());

        assertThrows(ServiceException.class, () -> {
            accountService.updateAvatar(account, part);
        });
    }

    @Test
    public void updateAvatar_persistenceExceptionThrownWhileCreatingConnectionManager_serviceException()
            throws PersistenceException, IOException {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("image.png");
        when(part.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        assertThrows(ServiceException.class, () -> {
            accountService.updateAvatar(account, part);
        });
    }

    @Test
    public void updateAvatar_testAccountNull_false() throws ServiceException {
        Part part = mock(Part.class);
        Account account = null;

        boolean result = accountService.updateAvatar(account, part);

        Assert.assertFalse(result);
    }

    @Test
    public void updateAvatar_partNull_false() throws ServiceException {
        Part part = null;
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);

        boolean result = accountService.updateAvatar(account, part);

        Assert.assertFalse(result);
    }

    @Test
    public void updateAvatar_invalidPartFileName_false() throws ServiceException {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("data.dat");
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, false, "salt", null);

        boolean result = accountService.updateAvatar(account, part);

        verify(part).getSubmittedFileName();
        Assert.assertFalse(result);
    }

    @Test
    public void changeAccountBlockState_validParameters_true() throws PersistenceException, ServiceException {
        long accountId = 2;
        String username = "username2";
        String hash = "hash2";
        String email = "email2@mail.com";
        Account account = new Account(accountId, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(accountDao.findEntityById(accountId)).thenReturn(account);
        AccountDao updater = mock(AccountDao.class);
        when(updater.update(account)).thenReturn(1);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

        boolean result = accountService.changeAccountBlockState(accountId, true);

        verify(updater).update(account);
        Assert.assertTrue(result);
    }

    @Test
    public void changeAccountBlockState_accountNull_false() throws PersistenceException, ServiceException {
        long accountId = 2;
        Account account = null;
        when(accountDao.findEntityById(accountId)).thenReturn(account);

        boolean result = accountService.changeAccountBlockState(accountId, true);

        verify(accountDao).findEntityById(accountId);
        Assert.assertFalse(result);
    }

    @Test
    public void changeAccountBlockState_persistenceExceptionThrownWhenFindingEntityById_serviceException()
            throws PersistenceException {
        long accountId = 2;
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(accountId)).thenThrow(new PersistenceException());

        assertThrows(ServiceException.class, () -> {
            accountService.changeAccountBlockState(accountId, true);
        });
    }

    @Test
    public void changeAccountBlockState_persistenceExceptionThrownWhenUpdating_serviceException() throws PersistenceException {
        long accountId = 2;
        String username = "username2";
        String hash = "hash2";
        String email = "email2@mail.com";
        Account account = new Account(accountId, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        when(accountDao.findEntityById(accountId)).thenReturn(account);
        AccountDao updater = mock(AccountDao.class);
        when(updater.update(account)).thenThrow(new PersistenceException());
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

        assertThrows(ServiceException.class, () -> {
            accountService.changeAccountBlockState(accountId, true);
        });
    }

    @Test
    public void changeAccountBlockState_persistenceExceptionThrownWhenCreatingConnectionManager_serviceException()
            throws PersistenceException {
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        assertThrows(ServiceException.class, () -> {
            accountService.changeAccountBlockState(2, true);
        });
    }

    @Test
    public void countAccounts_noPersistenceExceptionThrown_expectedCountReturned() throws PersistenceException, ServiceException {
        int accountCount = 4;
        when(accountDao.findAccountCount()).thenReturn(accountCount);
        int result = accountService.countAccounts();

        Assert.assertEquals(result, 4);
    }

    @Test
    public void findMessageById_persistenceExceptionThrown_serviceException() throws PersistenceException {
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        assertThrows(ServiceException.class, () -> {
            accountService.countAccounts();
        });
    }

    @Test
    public void findRatingPageAccounts_noPersistenceExceptionThrown_accountsFound() throws PersistenceException, ServiceException {
        int page = 1;
        int numberOfAccountsPerPage = 3;
        long accountId = 1;
        List<Account> expected = new ArrayList<>();
        expected.add(new Account(accountId, "username", "hash",
                "email", AccountType.ADMIN, 0, false, "salt", null));
        when(accountDao.findPageAccountsSortByRating(page, numberOfAccountsPerPage)).thenReturn(new ArrayList<>(expected));

        List<Account> actual = accountService.findRatingPageAccounts(page, numberOfAccountsPerPage);

        verify(accountDao).findPageAccountsSortByRating(page, numberOfAccountsPerPage);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void findRatingPageAccounts_persistenceExceptionThrown_serviceException() throws PersistenceException {
        int page = 1;
        int numberOfAccountsPerPage = 3;
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        assertThrows(ServiceException.class, () -> {
            accountService.findRatingPageAccounts(page, numberOfAccountsPerPage);
        });
    }

    @Test
    public void changeAccountType_validParameters_true() throws PersistenceException, ServiceException {
        String username = "username2";
        String hash = "hash2";
        String email = "email2@gmail.com";
        long accountId = 2;
        Account userAccount = new Account(accountId, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
        when(accountDao.update(userAccount)).thenReturn(1);

        boolean result = accountService.changeAccountType(accountId, AccountType.ADMIN);

        verify((accountDao)).update(userAccount);
        Assert.assertTrue(result);
    }

    @Test
    public void changeAccountType_noUpdate_false() throws PersistenceException, ServiceException {
        String username = "username2";
        String hash = "hash2";
        String email = "email2@gmail.com";
        long accountId = 2;
        Account userAccount = new Account(accountId, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
        when(accountDao.update(userAccount)).thenReturn(0);

        boolean result = accountService.changeAccountType(accountId, AccountType.ADMIN);

        verify(accountDao).update(userAccount);
        Assert.assertFalse(result);
    }

    @Test
    public void changeAccountType_accountNotFoundInDatabase_false() throws PersistenceException, ServiceException {
        long accountId = 2;
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(accountId)).thenReturn(null);

        boolean result = accountService.changeAccountType(accountId, AccountType.ADMIN);

        verify(accountDao2).findEntityById(accountId);
        Assert.assertFalse(result);
}

    @Test
    public void changeAccountType_persistenceExceptionThrown_serviceException() throws PersistenceException {
        String username = "username2";
        String hash = "hash2";
        String email = "email2@gmail.com";
        long accountId = 2;
        Account userAccount = new Account(accountId, username, hash,
                email, AccountType.USER, 0, false, "salt", null);
        AccountDao accountDao2 = mock(AccountDao.class);
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
        when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
        when(accountDao.update(userAccount)).thenThrow(new PersistenceException());

        assertThrows(ServiceException.class, () -> {
            accountService.changeAccountType(accountId, AccountType.ADMIN);
        });
    }

    @Test
    public void changeAccountType_persistenceExceptionThrownWhileCreatingConnectionManager_serviceException()
            throws PersistenceException {
        long accountId = 2;
        doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

        assertThrows(ServiceException.class, () -> {
            accountService.changeAccountType(accountId, AccountType.ADMIN);
        });
    }
}
