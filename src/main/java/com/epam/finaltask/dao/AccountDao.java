package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;

import java.util.List;

/**
 * Interface that contains methods specific to DAO that works with accounts.
 */
public interface AccountDao extends Dao<Account> {

    /**
     * Finds account by username.
     * @param username Username of the account to find
     * @return Account if it was found, else returns null
     * @throws PersistenceException if SQLException was thrown
     */
    Account findAccountByUsername(String username) throws PersistenceException;

    /**
     * Finds account by email
     * @param email Email of the account to find
     * @return Account if it was found, else returns null
     * @throws PersistenceException if SQLException was thrown
     */
    Account findAccountByEmail(String email) throws PersistenceException;

    /**
     * Finds account on the page sorted by rating.
     * @param startPage Page from which account will be taken.
     * @param numberOfAccountsPerPage Number of accounts per page
     * @return Account at chosen page
     * @throws PersistenceException if SQLException was thrown
     */
    List<Account> findPageAccountsSortByRating(int startPage, int numberOfAccountsPerPage) throws PersistenceException;

    /**
     * Returns database account count.
     * @return database account count
     * @throws PersistenceException if SQLException was thrown
     */
    int findAccountCount() throws PersistenceException;

    /**
     * Finds all database accounts by account type.
     * @param accountType Type of the account that account should have.
     * @return All accounts with chosen account type
     * @throws PersistenceException if SQLException was thrown
     */
    List<Account> findAllByAccountType(AccountType accountType) throws PersistenceException;
}
