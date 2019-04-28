package com.epam.finaltask.dao.impl;

import com.epam.finaltask.connectionpool.ConnectionPool;
import com.epam.finaltask.connectionpool.ConnectionPoolException;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager implements AutoCloseable {
    private Connection connection;

    public ConnectionManager() throws PersistenceException {
        try {
            connection = ConnectionPool.instance.getConnection();
        } catch (ConnectionPoolException e) {
            throw new PersistenceException("unable to get connection from the pool", e);
        }
    }

    public void disableAutoCommit() throws PersistenceException {
        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new PersistenceException("could not set auto commit to false", e);
        }
    }

    public void enableAutoCommit() throws PersistenceException {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenceException("could not set auto commit to true", e);
        }
    }

    public void commit() throws PersistenceException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new PersistenceException("could not commit", e);
        }
    }

    public void rollback() throws PersistenceException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new PersistenceException("could not rollback", e);
        }
    }

    @Override
    public void close() throws PersistenceException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new PersistenceException("unable to return connection to the pool", e);
        }
    }

    Connection getConnection() {
        return connection;
    }
}
