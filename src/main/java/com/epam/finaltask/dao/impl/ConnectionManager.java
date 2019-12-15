package com.epam.finaltask.dao.impl;

import com.epam.finaltask.connectionpool.ConnectionPool;
import com.epam.finaltask.connectionpool.ConnectionPoolException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class that instances are used to provide connection to DAOs.
 */
class   ConnectionManager extends AbstractConnectionManager {
    /**
     * Connection that is being provided.
     */
    private Connection connection;

    /**
     * Creates ConnectionManager that takes connection from the connection pool.
     * @throws PersistenceException if ConnectionPoolException is thrown
     */
    public ConnectionManager() throws PersistenceException {
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
        } catch (ConnectionPoolException e) {
            throw new PersistenceException("unable to get connection from the pool", e);
        }
    }

    /**
     * Disables auto commit.
     * @throws PersistenceException if SQLException is thrown
     */
    @Override
    public void disableAutoCommit() throws PersistenceException {
        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new PersistenceException("could not set auto commit to false", e);
        }
    }

    /**
     * Enables auto commit.
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public void enableAutoCommit() throws PersistenceException {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenceException("could not set auto commit to true", e);
        }
    }

    /**
     * Delegates commit call to the connection.
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public void commit() throws PersistenceException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new PersistenceException("could not commit", e);
        }
    }

    /**
     * Delegates rollback call to the connection.
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public void rollback() throws PersistenceException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new PersistenceException("could not rollback", e);
        }
    }

    /**
     * Delegates close call to the connection.
     * @throws PersistenceException If SQLException is thrown
     */
    @Override
    public void close() throws PersistenceException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new PersistenceException("unable to return connection to the pool", e);
        }
    }

    /**
     * Returns {@link ConnectionManager#connection}.
     * Used to provide connection to DAO.
     * @return {@link ConnectionManager#connection}
     */
    @Override
    Connection getConnection() {
        return connection;
    }
}
