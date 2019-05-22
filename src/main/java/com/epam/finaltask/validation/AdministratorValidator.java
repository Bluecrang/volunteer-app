package com.epam.finaltask.validation;

import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;

/**
 * Validator which is used to check if object is Account with administrator rights.
 */
public class AdministratorValidator {

    /**
     * Checks if object is account with account type {@link AccountType#ADMIN}.
     * @param object Object to validate
     * @return {@code true} if object is an account with account type {@link AccountType#ADMIN}, else returns {@code false}
     */
    public boolean validate(Object object) {
        if (object instanceof Account) {
            Account account = (Account) object;
            return (account.getAccountType() == AccountType.ADMIN);
        }
        return false;
    }
}
