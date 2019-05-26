package com.epam.finaltask.command.impl;

import com.epam.finaltask.entity.AccountType;

import java.util.*;

/**
 * Class, instances of that contain allowed account types and http methods to execute command with.
 * Contains nested builder class that should be used to create CommandConstraints instances.
 */
public class CommandConstraints {

    /**
     * Set of http methods with that command execution is allowed.
     */
    private Set<HttpMethodType> allowedMethods = new HashSet<>();

    /**
     * Types of accounts that are allowed to execute command.
     */
    private Set<AccountType> allowedAccountTypes = new HashSet<>();

    /**
     * Empty constructor.
     */
    private CommandConstraints() {
    }

    /**
     * Builder for CommandConstraints creation.
     */
    public static class CommandConstraintsBuilder {
        /**
         * Constraints object that will be built.
         */
        private CommandConstraints constraints = new CommandConstraints();

        /**
         * Empty constructor.
         */
        private CommandConstraintsBuilder() {
        }

        /**
         * Sets allowed http methods.
         * Does not set null values.
         * @param methods Methods to set
         * @return current builder
         */
        public CommandConstraintsBuilder buildHttpMethods(HttpMethodType ... methods) {
            for (HttpMethodType method : methods) {
                if (method != null) {
                    constraints.allowedMethods.add(method);
                }
            }
            return this;
        }

        /**
         * Sets allowed account types.
         * Does not set null values.
         * @param accountTypes Account types to set
         * @return current builder
         */
        public CommandConstraintsBuilder buildAccountTypes(AccountType ... accountTypes) {
            for (AccountType accountType : accountTypes) {
                if (accountType != null) {
                    constraints.allowedAccountTypes.add(accountType);
                }
            }
            return this;
        }

        /**
         * Builds CommandConstraints object.
         * @return Constructed CommandConstraints object
         */
        public CommandConstraints build() {
            return constraints;
        }
    }

    /**
     * Creates {@link CommandConstraintsBuilder} instance.
     * @return CommandConstraintsBuilder instance.
     */
    public static CommandConstraintsBuilder builder() {
        return new CommandConstraintsBuilder();
    }

    /**
     * Checks if method is allowed by constraints.
     * @param httpMethodType Method that is checked.
     * @return {@code true} if method is allowed, else returns {@code false}
     */
    public boolean checkIfAllowed(HttpMethodType httpMethodType) {
        return allowedMethods.contains(httpMethodType);
    }

    /**
     * Checks if account type is allowed by constraints.
     * @param accountType Account type that is checked
     * @return {@code true} if account type is allowed, else returns {@code false}
     */
    public boolean checkIfAllowed(AccountType accountType) {
        return allowedAccountTypes.contains(accountType);
    }
}
