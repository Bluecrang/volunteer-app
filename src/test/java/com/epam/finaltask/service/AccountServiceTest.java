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
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
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
    public void addValueToRatingTestFoundAccountNull() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        long accountId = 2;
        try {
            when(accountDao.findEntityById(accountId)).thenReturn(null);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        try {
            boolean result = accountService.addValueToRating(actingAccount, accountId, 2);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void addValueToRatingTestActingAccountNotAdmin() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        long accountId = 2;
        try {
            boolean result = accountService.addValueToRating(actingAccount, accountId, 2);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void addValueToRatingTestPersistenceExceptionThrownWhenFindingById() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        long accountId = 2;
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenThrow(new PersistenceException());

            accountService.addValueToRating(actingAccount, accountId, 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void addValueToRatingTestPersistenceExceptionThrownWhenUpdating() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
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

            accountService.addValueToRating(actingAccount, accountId, 2);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void addValueToRatingTestPersistenceExceptionThrownWhileCreatingConnectionManager() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account actingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        long accountId = 2;
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();

            accountService.addValueToRating(actingAccount, accountId, 2);
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

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void changeAccountBlockStateTestValidParameters() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(2, username2, hash2,
                email2, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(2)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenReturn(1);
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
    public void changeAccountBlockStateTestAccountNull() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        Account account = null;
        try {
            when(accountDao.findEntityById(2)).thenReturn(account);
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

    @Test
    public void changeAccountBlockStateTestBlockingAccountNotAdmin() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(2, username2, hash2,
                email2, AccountType.USER, 0, true, false, "salt", null);
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(2)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenReturn(1);
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

    @Test(expectedExceptions = ServiceException.class)
    public void changeAccountBlockStateTestPersistenceExceptionThrownWhenFindingEntityById() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(2)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.changeAccountBlockState(blockingAccount, 2, true);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void changeAccountBlockStateTestPersistenceExceptionThrownWhenUpdating() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@mail.com";
        Account account = new Account(2, username2, hash2,
                email2, AccountType.USER, 0, true, false, "salt", null);
        try {
            when(accountDao.findEntityById(2)).thenReturn(account);
            AccountDao updater = mock(AccountDao.class);
            when(updater.update(account)).thenThrow(new PersistenceException());
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao, updater);
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.changeAccountBlockState(blockingAccount, 2, true);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void changeAccountBlockStateTestPersistenceExceptionThrownWhenCreatingConnectionManager() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        Account blockingAccount = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.changeAccountBlockState(blockingAccount, 2, true);
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
        try {
            List<Account> expected = new ArrayList<>();
            expected.add(new Account(1, "username", "hash",
                    "email", AccountType.ADMIN, 0, true, false, "salt", null));
            when(accountDao.findPageAccountsSortByRating(page, numberOfAccountsPerPage)).thenReturn(new ArrayList<>(expected));
            
            List<Account> accountList = accountService.findRatingPageAccounts(page, numberOfAccountsPerPage);

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
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@gmail.com";
        long accountId = 2;
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            try {
                Account userAccount = new Account(2, username2, hash2,
                        email2, AccountType.USER, 0, true, false, "salt", null);
                AccountDao accountDao2 = mock(AccountDao.class);
                when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
                when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
                when(accountDao.update(userAccount)).thenReturn(1);
            } catch (PersistenceException e) {
                fail("Unexpected PersistenceException", e);
            }
            boolean result = accountService.promoteUserToAdmin(account, accountId);

            Assert.assertTrue(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void promoteUserToAdminTestNoUpdate() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@gmail.com";
        long accountId = 2;
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            try {
                Account userAccount = new Account(2, username2, hash2,
                        email2, AccountType.USER, 0, true, false, "salt", null);
                AccountDao accountDao2 = mock(AccountDao.class);
                when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
                when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
                when(accountDao.update(userAccount)).thenReturn(0);
            } catch (PersistenceException e) {
                fail("Unexpected PersistenceException", e);
            }
            boolean result = accountService.promoteUserToAdmin(account, accountId);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void promoteUserToAdminTestAccountNotFoundInDatabase() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 2;
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            try {
                AccountDao accountDao2 = mock(AccountDao.class);
                when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
                when(accountDao2.findEntityById(accountId)).thenReturn(null);
            } catch (PersistenceException e) {
                fail("Unexpected PersistenceException", e);
            }
            boolean result = accountService.promoteUserToAdmin(account, accountId);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void promoteUserToAdminTestSessionAccountNotAdmin() {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 2;
        Account account = new Account(1, username, hash,
                email, AccountType.USER, 0, true, false, "salt", null);
        try {
            boolean result = accountService.promoteUserToAdmin(account, accountId);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test
    public void promoteUserToAdminTestSessionAccountNull() {
        long accountId = 2;
        Account account = null;
        try {
            boolean result = accountService.promoteUserToAdmin(account, accountId);

            Assert.assertFalse(result);
        } catch (ServiceException e) {
            fail("Unexpected ServiceException", e);
        }
    }

    @Test(expectedExceptions = ServiceException.class)
    public void promoteUserToAdminTestPersistenceExceptionThrown() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        String username2 = "username2";
        String hash2 = "hash2";
        String email2 = "email2@gmail.com";
        long accountId = 2;
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            Account userAccount = new Account(2, username2, hash2,
                    email2, AccountType.USER, 0, true, false, "salt", null);
            AccountDao accountDao2 = mock(AccountDao.class);
            when(daoFactory.createAccountDao(connectionManager)).thenReturn(accountDao2, accountDao);
            when(accountDao2.findEntityById(accountId)).thenReturn(userAccount);
            when(accountDao.update(userAccount)).thenThrow(new PersistenceException());
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.promoteUserToAdmin(account, accountId);
    }

    @Test(expectedExceptions = ServiceException.class)
    public void promoteUserToAdminTestPersistenceExceptionThrownWhileCreatingConnectionManager() throws ServiceException {
        String username = "username";
        String hash = "hash";
        String email = "email@mail.com";
        long accountId = 2;
        Account account = new Account(1, username, hash,
                email, AccountType.ADMIN, 0, true, false, "salt", null);
        try {
            doThrow(new PersistenceException()).when(connectionManagerFactory).createConnectionManager();
        } catch (PersistenceException e) {
            fail("Unexpected PersistenceException", e);
        }
        accountService.promoteUserToAdmin(account, accountId);
    }
}
