package com.epam.finaltask.service;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.AccountType;
import com.epam.finaltask.entity.Account;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.Part;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
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
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        try {
            when(connectionManagerFactory.createConnectionManager()).thenReturn(connectionManager);
        } catch (PersistenceException e) {
            throw new RuntimeException("Unexpected exception while performing setUp", e);
        }
        when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao);

        accountService = new AccountService(daoFactory, connectionManagerFactory);
    }

    @Test
    public void addValueToRatingTestValidParameters() {
        long accountId = 2;
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, username2, hash2,
                email2, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenReturn(1);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

            boolean result = accountService.addValueToRating(accountId, 2);

            verify(accountDao).findEntityById(accountId);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void addValueToRatingTestFoundAccountNull() {
        long accountId = 2;
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(null);

            boolean result = accountService.addValueToRating(accountId, 2);

            verify(accountDao).findEntityById(accountId);
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void addValueToRatingTestPersistenceExceptionThrownWhenFindingById() throws ServiceException {
        long accountId = 2;
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenThrow(new PersistenceException());

            accountService.addValueToRating(accountId, 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void addValueToRatingTestPersistenceExceptionThrownWhenUpdating() throws ServiceException {
        long accountId = 2;
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(accountId, username2, hash2,
                email2, AccountType.USER, 0, true, false, "salt", null);
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(accountDao2.findEntityById(accountId)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenThrow(new PersistenceException());
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, updater);

            accountService.addValueToRating(accountId, 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void addValueToRatingTestPersistenceExceptionThrownWhileCreatingConnectionManager() throws ServiceException {
        long accountId = 2;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

            accountService.addValueToRating(accountId, 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
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
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(1)).thenReturn(account);
            when(accountDao.update(account)).thenReturn(1);

            boolean result = accountService.updateAvatar(account, part);

            verify(accountDao).update(account);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void updateAvatarTestIOExceptionThrownWhileGettingStream() throws ServiceException {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("image.png");
        try {
            when(part.getInputStream()).thenThrow(new IOException());
        } catch (IOException e) {
            fail("Unexpected IOException", e);
        }
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(1)).thenReturn(account);
            when(accountDao.update(account)).thenReturn(1);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.updateAvatar(account, part);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void updateAvatarTestPersistenceExceptionThrown() throws ServiceException {
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
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(1)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.updateAvatar(account, part);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void updateAvatarTestPersistenceExceptionThrownWhileCreatingConnectionManager() throws ServiceException {
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
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.updateAvatar(account, part);
    }

    @Test
    public void updateAvatarTestAccountNull() {
        Part part = mock(Part.class);
        Account account = null;
        try {
            boolean result = accountService.updateAvatar(account, part);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void updateAvatarTestPartNull() {
        Part part = null;
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            boolean result = accountService.updateAvatar(account, part);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void updateAvatarTestInvalidPartFileName() {
        Part part = mock(Part.class);
        when(part.getSubmittedFileName()).thenReturn("data.dat");
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            boolean result = accountService.updateAvatar(account, part);

            verify(part).getSubmittedFileName();
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void changeAccountBlockStateTestValidParameters() {
        long accountId = 2;
        String username = "username2";
        String hash = "hash2";
        String email = "email2@mail.com";
        Account account = new Account(accountId, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenReturn(1);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);

            boolean result = accountService.changeAccountBlockState(accountId, true);

            verify(updater).update(account);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void changeAccountBlockStateTestAccountNull() {
        long accountId = 2;
        Account account = null;
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(account);

            boolean result = accountService.changeAccountBlockState(accountId, true);

            verify(accountDao).findEntityById(accountId);
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void changeAccountBlockStateTestPersistenceExceptionThrownWhenFindingEntityById() throws ServiceException {
        long accountId = 2;
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.changeAccountBlockState(accountId, true);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void changeAccountBlockStateTestPersistenceExceptionThrownWhenUpdating() throws ServiceException {
        long accountId = 2;
        String username = "username2";
        String hash = "hash2";
        String email = "email2@mail.com";
        Account account = new Account(accountId, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenThrow(new PersistenceException());
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.changeAccountBlockState(accountId, true);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void changeAccountBlockStateTestPersistenceExceptionThrownWhenCreatingConnectionManager() throws ServiceException {
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.changeAccountBlockState(2, true);
    }

    @Test
    public void countAccountsTestNoPersistenceExceptionThrown() {
        int accountCount = 4;
        try {
            when(accountDao.findAccountCount()).thenReturn(accountCount);
            int result = accountService.countAccounts();
            
            Assert.assertEquals(result, 4);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findMessageByIdTestPersistenceExceptionThrown() throws ServiceException {
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

            accountService.countAccounts();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void findRatingPageAccountsTestNoPersistenceExceptionThrown() {
        int page = 1;
        int numberOfAccountsPerPage = 3;
        long accountId = 1;
        try {
            List<Account> expected = new ArrayList<>();
            expected.add(new Account(accountId, "username", "hash",
                    "email", AccountType.ADMIN, 0, true, false, "salt", null));
            when(accountDao.findPageAccountsSortByRating(page, numberOfAccountsPerPage)).thenReturn(new ArrayList<>(expected));
            
            List<Account> accountList = accountService.findRatingPageAccounts(page, numberOfAccountsPerPage);

            verify(accountDao).findPageAccountsSortByRating(page, numberOfAccountsPerPage);
            Assert.assertEquals(accountList, expected);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void findRatingPageAccountsTestPersistenceExceptionThrown() throws ServiceException {
        int page = 1;
        int numberOfAccountsPerPage = 3;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

            accountService.findRatingPageAccounts(page, numberOfAccountsPerPage);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void promoteUserToAdminTestValidParameters() {
        String username = "username2";
        String hash = "hash2";
        String email = "email2@gmail.com";
        long accountId = 2;
        try {
            Account userAccount = new Account(accountId, username, hash,
                    email, AccountType.USER, 0, true, false, "salt", null);
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
            when(accountDao.update(userAccount)).thenReturn(1);

            boolean result = accountService.promoteUserToAdmin(accountId);

            verify((accountDao)).update(userAccount);
            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test
    public void promoteUserToAdminTestNoUpdate() {
        String username = "username2";
        String hash = "hash2";
        String email = "email2@gmail.com";
        long accountId = 2;
        try {
            Account userAccount = new Account(accountId, username, hash,
                    email, AccountType.USER, 0, true, false, "salt", null);
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
            when(accountDao.update(userAccount)).thenReturn(0);

            boolean result = accountService.promoteUserToAdmin(accountId);

            verify(accountDao).update(userAccount);
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
            } catch (PersistenceException e) {
                fail("Unexpected PersistenceException", e);
            }
    }

    @Test
    public void promoteUserToAdminTestAccountNotFoundInDatabase() {
        long accountId = 2;
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenReturn(null);

            boolean result = accountService.promoteUserToAdmin(accountId);

            verify(accountDao2).findEntityById(accountId);
            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void promoteUserToAdminTestPersistenceExceptionThrown() throws ServiceException {
        String username = "username2";
        String hash = "hash2";
        String email = "email2@gmail.com";
        long accountId = 2;
        try {
            Account userAccount = new Account(accountId, username, hash,
                    email, AccountType.USER, 0, true, false, "salt", null);
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
            when(accountDao.update(userAccount)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.promoteUserToAdmin(accountId);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void promoteUserToAdminTestPersistenceExceptionThrownWhileCreatingConnectionManager() throws ServiceException {
        long accountId = 2;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.promoteUserToAdmin(accountId);
    }
}
