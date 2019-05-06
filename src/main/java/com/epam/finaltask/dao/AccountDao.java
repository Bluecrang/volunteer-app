package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.PersistenceException;
import com.epam.finaltask.entity.Account;

public interface AccountDao extends Dao<Account> {

    Account findAccountByLogin(String login) throws PersistenceException;
    Account findAccountByEmail(String email) throws PersistenceException;
}
