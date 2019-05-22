package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.entity.Account;
import com.epam.finaltask.entity.AccountType;
import org.apache.commons.codec.binary.Base64;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

/**
 * Class which implements CRUD and several other operations to work with database accounts.
 */
class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {

    private static final String FIND_ALL_ACCOUNTS = "SELECT acc.account_id, acc.username, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id";
    private static final String FIND_ACCOUNT_BY_ID = "SELECT acc.account_id, acc.username, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc.account_id = ?";
    private static final String FIND_ACCOUNT_BY_USERNAME = "SELECT acc.account_id, acc.username, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc.username = ?";
    private static final String FIND_ACCOUNT_BY_EMAIL = "SELECT acc.account_id, acc.username, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc.email = ?";
    private static final String INSERT_ACCOUNT = "INSERT INTO account(username, password, email, account_type_id, rating, " +
            "verified, blocked, salt, avatar) " +
            "VALUES(?,?,?,(SELECT account_type_id FROM account_type WHERE type = ?),?,?,?,?,?)";
    private static final String DELETE_ACCOUNT_BY_ID = "DELETE FROM account where account_id = ?";
    private static final String UPDATE_ACCOUNT_BY_ID = "UPDATE account " +
            "SET username = ?, password = ?, email = ?, account_type_id = (SELECT account_type_id FROM account_type WHERE type = ?), " +
            "rating = ?, verified = ?, blocked = ?, salt = ?, avatar = ? " +
            "WHERE account_id = ?";
    private static final String FIND_ACCOUNTS_IN_RANGE_SORT_BY_RATING = "SELECT acc.account_id, acc.username, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "ORDER BY acc.rating DESC " +
            "LIMIT ? OFFSET ?";
    private static final String FIND_ACCOUNT_COUNT = "SELECT COUNT(account_id) FROM account";
    private static final String FIND_ALL_BY_ACCOUNT_TYPE = "SELECT acc.account_id, acc.username, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc_type.type = ?";

    /**
     * Creates AccountDaoImpl using connection provided by connectionManager.
     * @param connectionManager Connection manager which provides connection to the DAO
     */
    public AccountDaoImpl(AbstractConnectionManager connectionManager) {
        super(connectionManager);
    }

    /**
     * Returns database account count.
     * @return Database account count
     * @throws PersistenceException If SQLException is thrown
     */
    public int findAccountCount() throws PersistenceException {
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNT_COUNT)){
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding page accounts sorted by rating", e);
        }
        return 0;
    }

    /**
     * Finds account on page sorted by rating.
     * @param page page to show accounts from
     * @param numberOfAccountsPerPage Number of account on each page
     * @return Accounts from the page
     * @throws PersistenceException If SQLException is thrown
     */
    public List<Account> findPageAccountsSortByRating(int page, int numberOfAccountsPerPage) throws PersistenceException {
        List<Account> accountList = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNTS_IN_RANGE_SORT_BY_RATING)){
            statement.setInt(1, numberOfAccountsPerPage);
            statement.setInt(2, (page - 1) * numberOfAccountsPerPage);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    accountList.add(createAccountFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding page accounts sorted by rating", e);
        }
        return accountList;
    }

    /**
     * Finds account by username.
     * @param username Account's username
     * @return Account if it exists, else returns null
     * @throws PersistenceException If SQLException is thrown
     */
    public Account findAccountByUsername(String username) throws PersistenceException {
        if (username == null) {
            return null;
        }
        Account account = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNT_BY_USERNAME)){
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    account = createAccountFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding by id", e);
        }
        return account;
    }

    /**
     * Finds account by email.
     * @param email Account's email
     * @return Account if it exists, else returns null
     * @throws PersistenceException If SQLException is thrown
     */
    public Account findAccountByEmail(String email) throws PersistenceException {
        if (email == null) {
            return null;
        }
        Account account = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNT_BY_EMAIL)){
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    account = createAccountFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding by email", e);
        }
        return account;
    }

    /**
     * Finds all accounts with chosen account type.
     * @param accountType Account type of accounts to return
     * @return All accounts with chosen account type
     * @throws PersistenceException If SQLException is thrown
     */
    public List<Account> findAllByAccountType(AccountType accountType) throws PersistenceException {
        List<Account> list = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ALL_BY_ACCOUNT_TYPE)){
            statement.setString(1, accountType.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(createAccountFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding accounts by account type", e);
        }
        return list;
    }

    /**
     * Returns all database accounts.
     * @return All database accounts
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public List<Account> findAll() throws PersistenceException {
        List<Account> list = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ALL_ACCOUNTS);
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                list.add(createAccountFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding all accounts", e);
        }
        return list;
    }

    /**
     * Finds entity by id.
     * @param id Id of the entity to find
     * @return Account which chosen id if it exists, else returns null
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public Account findEntityById(long id) throws PersistenceException {
        Account account = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNT_BY_ID)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    account = createAccountFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while finding by id", e);
        }
        return account;
    }

    /**
     * Returns query which deletes account by id.
     * @return Query which deletes account by id.
     */
    @Override
    public String getDeleteByIdQuery() {
        return DELETE_ACCOUNT_BY_ID;
    }

    /**
     * Adds account to the database.
     * @param entity Account to add to the database
     * @return {@code true} if account was successfully created, else returns {@code false}
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public boolean create(Account entity) throws PersistenceException {
        if (entity == null) {
            return false;
        }
        try (PreparedStatement statement = getConnection().prepareStatement(INSERT_ACCOUNT)) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPasswordHash());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getAccountType().name());
            statement.setInt(5, entity.getRating());
            statement.setBoolean(6, entity.isVerified());
            statement.setBoolean(7, entity.isBlocked());
            statement.setString(8, entity.getSalt());
            byte[] avatar = Base64.decodeBase64(entity.getAvatarBase64());
            if (avatar != null) {
                statement.setBlob(9, new SerialBlob(avatar));
            } else {
                statement.setNull(9, Types.BLOB);
            }
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while inserting account", e);
        }
    }

    /**
     * Updates account in the database.
     * @param entity Account to update
     * @return {@code true} if account was successfully updated, else returns {@code false}
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public int update(Account entity) throws PersistenceException {
        if (entity == null) {
            return 0;
        }
        try (PreparedStatement statement = getConnection().prepareStatement(UPDATE_ACCOUNT_BY_ID)){
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPasswordHash());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getAccountType().name());
            statement.setInt(5,entity.getRating());
            statement.setBoolean(6, entity.isVerified());
            statement.setBoolean(7, entity.isBlocked());
            statement.setString(8, entity.getSalt());
            byte[] avatar = Base64.decodeBase64(entity.getAvatarBase64());
            if (avatar != null) {
                statement.setBlob(9, new SerialBlob(avatar));
            } else {
                statement.setNull(9, Types.BLOB);
            }
            statement.setLong(10, entity.getAccountId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while updating", e);
        }
    }

    /**
     * Creates account using data from result set.
     * @param resultSet Result set which provides data to create account
     * @return Created account
     * @throws SQLException If SQLException is thrown
     */
    private Account createAccountFromResultSet(ResultSet resultSet) throws SQLException {
        long accountId = resultSet.getLong(1);
        String username = resultSet.getString(2);
        String passwordHash = resultSet.getString(3);
        String email = resultSet.getString(4);
        String accountType = resultSet.getString(5);
        int rating = resultSet.getInt(6);
        boolean verified = resultSet.getBoolean(7);
        boolean blocked = resultSet.getBoolean(8);
        String salt = resultSet.getString(9);
        byte[] avatar = resultSet.getBytes(10);
        String avatarBase64;
        if (avatar != null) {
            avatarBase64 = Base64.encodeBase64String(avatar);
        } else {
            avatarBase64 = null;
        }
        return new Account(accountId, username, passwordHash, email, AccountType.valueOf(accountType.toUpperCase()),
                rating, verified, blocked, salt, avatarBase64);
    }
}
