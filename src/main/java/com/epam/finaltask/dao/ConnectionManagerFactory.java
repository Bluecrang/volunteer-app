package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;

/**
 * Factory which creates {@link AbstractConnectionManager} subclass instances.
 */
public interface ConnectionManagerFactory {

    /**
     * Creates {@link AbstractConnectionManager} subclass instance.
     * @return {@link AbstractConnectionManager} subclass instance
     * @throws PersistenceException If SQLException is thrown while creating ConnectionManager
     */
    AbstractConnectionManager createConnectionManager() throws PersistenceException;
}
