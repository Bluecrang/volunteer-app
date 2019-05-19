package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;

import java.util.List;

public interface AccountDao extends Dao<Account> {

    Account findAccountByUsername(String login) throws PersistenceException;
    Account findAccountByEmail(String email) throws PersistenceException;
    List<Account> findPageAccountsSortByRating(int startPage, int numberOfAccountsPerPage) throws PersistenceException;
    int findAccountCount() throws PersistenceException;
    public List<Account> findAllByAccountType(AccountType accountType) throws PersistenceException;
}
