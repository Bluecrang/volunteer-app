package com.epam.finaltask.dao.impl;

import com.epam.finaltask.dao.ConnectionManagerFactory;

public class ConnectionManagerFactoryImpl implements ConnectionManagerFactory {
    @Override
    public AbstractConnectionManager createConnectionManager() throws PersistenceException {
        return new ConnectionManager();
    }
}
