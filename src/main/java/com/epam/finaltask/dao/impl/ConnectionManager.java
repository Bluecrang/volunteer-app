package com.epam.finaltask.dao.impl;

import com.epam.finaltask.connectionpool.ConnectionPool;
import com.epam.finaltask.connectionpool.ConnectionPoolException;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionManager extends AbstractConnectionManager {
    private Connection connection;

    public ConnectionManager() throws PersistenceException {
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
        } catch (ConnectionPoolException e) {
            throw new PersistenceException("unable to get connection from the pool", e);
        }
    }

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

    @Override
    public void commit() throws PersistenceException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new PersistenceException("could not commit", e);
        }
    }

    @Override
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

    @Override
    Connection getConnection() {
        return connection;
    }
}
