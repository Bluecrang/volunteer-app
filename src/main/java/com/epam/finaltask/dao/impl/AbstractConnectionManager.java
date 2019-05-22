package com.epam.finaltask.dao.impl;

import java.sql.Connection;

/**
 * Abstract class which subclasses are intended to provide connection manipulation methods.
 * Used by service layer to provide connection to DAOs.
 */
public abstract class AbstractConnectionManager implements AutoCloseable {

    public abstract void disableAutoCommit() throws PersistenceException;
    public abstract void enableAutoCommit() throws PersistenceException;
    public abstract void commit() throws PersistenceException;
    public abstract void rollback() throws PersistenceException;
    public abstract void close() throws PersistenceException;
    abstract Connection getConnection();
}
