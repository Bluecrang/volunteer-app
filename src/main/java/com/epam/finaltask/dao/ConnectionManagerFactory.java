package com.epam.finaltask.dao;

import com.epam.finaltask.dao.impl.AbstractConnectionManager;
import com.epam.finaltask.dao.impl.PersistenceException;

public interface ConnectionManagerFactory {

    AbstractConnectionManager createConnectionManager() throws PersistenceException;
}
