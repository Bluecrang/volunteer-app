package com.epam.finaltask.dao.impl;

import java.sql.Connection;

public abstract class AbstractConnectionManager implements AutoCloseable {

    public abstract void disableAutoCommit() throws PersistenceException;
    public abstract  void enableAutoCommit() throws PersistenceException;
    public abstract void commit() throws PersistenceException;
    public abstract void rollback() throws PersistenceException;
    public abstract void close() throws PersistenceException;
    abstract Connection getConnection();
}
