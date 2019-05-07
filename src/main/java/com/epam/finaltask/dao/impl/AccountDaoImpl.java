package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.AccountDao;
import com.epam.finaltask.entity.AccessLevel;
import com.epam.finaltask.entity.Account;
import org.apache.commons.codec.binary.Base64;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {

    private static final String FIND_ALL_ACCOUNTS = "SELECT acc.account_id, acc.login, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id";
    private static final String FIND_ACCOUNT_BY_ID = "SELECT acc.account_id, acc.login, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc.account_id = ?";
    private static final String FIND_ACCOUNT_BY_LOGIN = "SELECT acc.account_id, acc.login, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc.login = ?";
    private static final String FIND_ACCOUNT_BY_EMAIL = "SELECT acc.account_id, acc.login, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "WHERE acc.email = ?";
    private static final String INSERT_ACCOUNT = "INSERT INTO account(login, password, email, account_type_id, rating, " +
            "verified, blocked, salt, avatar) " +
            "VALUES(?,?,?,(SELECT account_type_id FROM account_type WHERE type = ?),?,?,?,?,?)";
    private static final String DELETE_ACCOUNT_BY_ID = "DELETE FROM account where account_id = ?";
    private static final String UPDATE_ACCOUNT_BY_ID = "UPDATE account " +
            "SET login = ?, password = ?, email = ?, account_type_id = (SELECT account_type_id FROM account_type WHERE type = ?), " +
            "rating = ?, verified = ?, blocked = ?, salt = ?, avatar = ? " +
            "WHERE account_id = ?";
    private static final String FIND_ACCOUNTS_IN_RANGE_SORT_BY_RATING = "SELECT acc.account_id, acc.login, acc.password, acc.email, " +
            "acc_type.type, acc.rating, acc.verified, acc.blocked, acc.salt, acc.avatar " +
            "FROM account acc INNER JOIN account_type acc_type on acc.account_type_id = acc_type.account_type_id " +
            "ORDER BY acc.rating DESC " +
            "LIMIT ? OFFSET ?";
    private static final String FIND_ACCOUNT_COUNT = "SELECT COUNT(account_id) FROM account";

    public AccountDaoImpl(ConnectionManager connectionManager) {
        super(connectionManager);
    }

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

    public List<Account> findPageAccountsSortByRating(int startPage, int numberOfAccountsPerPage) throws PersistenceException {
        List<Account> accountList = new LinkedList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNTS_IN_RANGE_SORT_BY_RATING)){
            statement.setInt(1, numberOfAccountsPerPage);
            statement.setInt(2, (startPage - 1) * numberOfAccountsPerPage);
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

    public Account findAccountByLogin(String login) throws PersistenceException {
        if (login == null) {
            return null;
        }
        Account account = null;
        try (PreparedStatement statement = getConnection().prepareStatement(FIND_ACCOUNT_BY_LOGIN)){
            statement.setString(1, login);
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

    private Account createAccountFromResultSet(ResultSet resultSet) throws SQLException {
        long accountId = resultSet.getLong(1);
        String login = resultSet.getString(2);
        String passwordHash = resultSet.getString(3);
        String email = resultSet.getString(4);
        String accountType = resultSet.getString(5);
        int rating = resultSet.getInt(6);
        boolean verified = resultSet.getBoolean(7);
        boolean blocked = resultSet.getBoolean(8);
        String salt = resultSet.getString(9);
        String avatar = Base64.encodeBase64String(resultSet.getBytes(10));
        return new Account(accountId, login, passwordHash, email, AccessLevel.valueOf(accountType.toUpperCase()),
                rating, verified, blocked, salt, avatar);
    }

    @Override
    public List<Account> findAll() throws PersistenceException {
        List<Account> list = new ArrayList<>();
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

    @Override
    public String getDeleteByIdQuery() {
        return DELETE_ACCOUNT_BY_ID;
    }

    @Override
    public boolean create(Account entity) throws PersistenceException {
        if (entity == null) {
            return false;
        }
        try (PreparedStatement statement = getConnection().prepareStatement(INSERT_ACCOUNT)) {
            statement.setString(1, entity.getLogin());
            statement.setString(2, entity.getPasswordHash());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getAccessLevel().name());
            statement.setInt(5, entity.getRating());
            statement.setBoolean(6, entity.isVerified());
            statement.setBoolean(7, entity.isBlocked());
            statement.setString(8, entity.getSalt());
            statement.setBlob(9, new SerialBlob(Base64.decodeBase64(entity.getAvatarBase64())));
            int result = statement.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while inserting account", e);
        }
    }

    @Override
    public int update(Account entity) throws PersistenceException {
        if (entity == null) {
            return 0;
        }
        try (PreparedStatement statement = getConnection().prepareStatement(UPDATE_ACCOUNT_BY_ID)){
            statement.setString(1, entity.getLogin());
            statement.setString(2, entity.getPasswordHash());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getAccessLevel().name());
            statement.setInt(5,entity.getRating());
            statement.setBoolean(6, entity.isVerified());
            statement.setBoolean(7, entity.isBlocked());
            statement.setString(8, entity.getSalt());
            statement.setBlob(9, new SerialBlob(Base64.decodeBase64(entity.getAvatarBase64())));
            statement.setLong(10, entity.getAccountId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("SQLException while updating", e);
        }
    }
}
