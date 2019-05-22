package com.epam.finaltask.service;

import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.ConnectionManagerFactoryImpl;
import com.epam.finaltask.dao.impl.DaoFactoryImpl;

/**
 * Provides basic service implementation. Contains {@link #daoFactory} and {@link #connectionManagerFactory} field and
 * constructors to specify their content.
 */
public abstract class AbstractService {

    /**
     * Factory which is used to create new DAO objects.
     */
    protected DaoFactory daoFactory;

    /**
     * Factory which is used to create {@link com.epam.finaltask.dao.impl.AbstractConnectionManager} implementation.
     */
    protected ConnectionManagerFactory connectionManagerFactory;

    /**
     * Constructor in which {@link #daoFactory} and {@link #connectionManagerFactory} used by service can be specified.
     * Assigns new {@link DaoFactoryImpl} object to {@link #daoFactory} field, if daoFactory parameter is null.
     * Assigns new {@link ConnectionManagerFactoryImpl} object to {@link #connectionManagerFactory} field,
     * if connectionManagerFactory parameter is null.
     * @param daoFactory               Factory to create DAOs
     * @param connectionManagerFactory Factory to create subclasses of the {@link com.epam.finaltask.dao.impl.AbstractConnectionManager}
     */
    public AbstractService(DaoFactory daoFactory, ConnectionManagerFactory connectionManagerFactory) {
        if (daoFactory != null) {
            this.daoFactory = daoFactory;
        } else {
            this.daoFactory = new DaoFactoryImpl();
        }
        if (connectionManagerFactory != null) {
            this.connectionManagerFactory = connectionManagerFactory;
        } else {
            this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
        }
    }

    /**
     * Constructor in which {@link #daoFactory} used by service can be specified.
     * Assigns new {@link ConnectionManagerFactoryImpl} object to {@link #connectionManagerFactory} field.
     * Assigns new {@link DaoFactoryImpl} object to {@link #daoFactory} field, if daoFactory parameter is null.
     * @param daoFactory Factory to create DAOs
     */
    public AbstractService(DaoFactory daoFactory) {
        if (daoFactory != null) {
            this.daoFactory = daoFactory;
        } else {
            this.daoFactory = new DaoFactoryImpl();
        }
        this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
    }

    /**
     * Constructor in which {@link #connectionManagerFactory} used by service can be specified.
     * Assigns new {@link DaoFactoryImpl} object to {@link #daoFactory} field.
     * Assigns new {@link ConnectionManagerFactoryImpl} object to {@link #connectionManagerFactory} field,
     * if connectionManagerFactory parameter is null.
     * @param connectionManagerFactory Factory to create subclass of {@link com.epam.finaltask.dao.impl.AbstractConnectionManager}
     */
    public AbstractService(ConnectionManagerFactory connectionManagerFactory) {
        if (connectionManagerFactory != null) {
            this.connectionManagerFactory = connectionManagerFactory;
        } else {
            this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
        }
        daoFactory = new DaoFactoryImpl();
    }

    /**
     * Constructor which creates {@link DaoFactoryImpl} and {@link ConnectionManagerFactoryImpl} and assign them to
     * corresponding fields
     */
    public AbstractService() {
        daoFactory = new DaoFactoryImpl();
        connectionManagerFactory = new ConnectionManagerFactoryImpl();
    }
}
