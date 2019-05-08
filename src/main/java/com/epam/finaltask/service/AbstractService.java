package com.epam.finaltask.service;

import com.epam.finaltask.dao.ConnectionManagerFactory;
import com.epam.finaltask.dao.DaoFactory;
import com.epam.finaltask.dao.impl.ConnectionManagerFactoryImpl;
import com.epam.finaltask.dao.impl.DaoFactoryImpl;

public abstract class AbstractService {

    protected DaoFactory daoFactory;
    protected ConnectionManagerFactory connectionManagerFactory;

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

    public AbstractService(DaoFactory daoFactory) {
        if (daoFactory != null) {
            this.daoFactory = daoFactory;
        } else {
            this.daoFactory = new DaoFactoryImpl();
        }
        this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
    }

    public AbstractService(ConnectionManagerFactory connectionManagerFactory) {
        if (connectionManagerFactory != null) {
            this.connectionManagerFactory = connectionManagerFactory;
        } else {
            this.connectionManagerFactory = new ConnectionManagerFactoryImpl();
        }
        daoFactory = new DaoFactoryImpl();
    }

    public AbstractService() {
        daoFactory = new DaoFactoryImpl();
        connectionManagerFactory = new ConnectionManagerFactoryImpl();
    }
}
