package com.epam.finaltask.validation;

import com.epam.finaltask.command.impl.CommandConstraints;
import com.epam.finaltask.command.impl.CommandData;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;

/**
 * Class which is used to check if command is allowed to be executed by user and with chosen http method.
 */
public class CommandDataValidator { //todo add tests

    /**
     * Checks if account type and http method are allowed by constraints.
     * @param data Data which is used by command
     * @param constraints Object which contains allowed account types and http method
     * @return {@code true}, if account type and http method are valid, else returns {@code false}
     */
    public boolean validate(CommandData data, CommandConstraints constraints) {
        if (constraints.checkIfAllowed(data.getMethod())) {
            Account sessionAccount = data.getSessionAccount();
            AccountType accountType;
            if (sessionAccount != null) {
                accountType = sessionAccount.getAccountType();
            } else {
                accountType = AccountType.GUEST;
            }
            return constraints.checkIfAllowed(accountType);
        }
        return false;
    }
}
