package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.ConnectionManagerFactory;

/**
 * Factory for {@link ConnectionManager} creation.
 */
public class ConnectionManagerFactoryImpl implements ConnectionManagerFactory {
    /**
     * Creates {@link ConnectionManager}.
     * @return {@link ConnectionManager}
     * @throws PersistenceException if ConnectionManager's no-argument constructor thrown a PersistenceException
     */
    @Override
    public AbstractConnectionManager createConnectionManager() throws PersistenceException {
        return new ConnectionManager();
    }
}
