package com.epam.finaltask.connectionpool;

import com.epam.finaltask.validation.FileValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum ConnectionPool {
    instance;

    private final Logger logger;

    private PoolConfig config;
    private BlockingQueue<ProxyConnection> idlingConnections = new LinkedBlockingQueue<>();
    private Set<ProxyConnection> allConnections = ConcurrentHashMap.newKeySet();
    private Lock lock = new ReentrantLock();
    private boolean closed;

    ConnectionPool() {
        logger = LogManager.getLogger();
    }

    public void init(String configFilename, int maintenancePeriod) {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            logger.log(Level.FATAL, "could not register database driver", e);
            throw new RuntimeException(e);
        }
        logger.log(Level.INFO, "database driver successfully registered");

        FileValidator fileValidator = new FileValidator();
        if (!fileValidator.validate(configFilename)) {
            String message = "config file is not valid";
            logger.log(Level.FATAL, message);
            throw new RuntimeException(message);
        }
        PropertiesReader reader = new PropertiesReader();
        Properties propertiesConfig = reader.readProperties(configFilename);
        config = new PoolConfig(propertiesConfig);

        for (int i = 0; i < config.getPoolSize(); i++) {
            addConnection();
        }

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new PoolSupervisor(), maintenancePeriod, maintenancePeriod);
    }

    public Connection getConnection() throws ConnectionPoolException {
        if (closed) {
            throw new ConnectionPoolException("Could not get connection: pool is closed");
        }
        try {
            ProxyConnection connection = idlingConnections.take();
            while (connection.isClosed()) {
                allConnections.remove(connection);
                addConnection();
                connection = idlingConnections.take();
            }
            logger.log(Level.DEBUG, "connection " + connection + " provided");
            return connection;
        } catch (InterruptedException exception) {
            throw new ConnectionPoolException("InterruptedException while getting connection", exception);
        } catch (SQLException exception) {
            throw new ConnectionPoolException("SQLException while getting connection", exception);
        }
    }

    public void closePool() {
        try {
            lock.lock();
            closed = true;
            for (int i = 0; i < allConnections.size(); i++) {
                try {
                    ProxyConnection connection = idlingConnections.take();
                    connection.closeInnerConnection();
                } catch (SQLException exception) {
                    logger.log(Level.ERROR, "SQLException while closing connection", exception);
                } catch (InterruptedException exception) {
                    logger.log(Level.ERROR, "InterruptedException while taking connection", exception);
                }
            }
            deregisterDrivers();
        } finally {
            lock.unlock();
        }
    }

    void releaseConnection(ProxyConnection connection) {
        try {
            if (allConnections.contains(connection) && !connection.isClosed()) {
                idlingConnections.put(connection);
                logger.log(Level.DEBUG, "connection " + connection + " released");
            }
        } catch (InterruptedException exception) {
            logger.log(Level.ERROR, "InterruptedException thrown while releasing connection", exception);
        } catch (SQLException exception) {
            logger.log(Level.ERROR, "unable to check if connection is closed", exception);
        }
    }

    void addConnection() {
        try {
            if (!closed) {
                ProxyConnection connection = new ProxyConnection(DriverManager.getConnection(
                        config.getDatabaseUrl(),
                        config.getUser(),
                        config.getPassword()));
                idlingConnections.put(connection);
                allConnections.add(connection);
                logger.log(Level.DEBUG, "connection " + connection + " created and added to the pool");
            }
        } catch (SQLException exception) {
            logger.log(Level.ERROR, "could not create connection", exception);
        } catch (InterruptedException exception) {
            logger.log(Level.ERROR, "could not add connection", exception);
        }
    }

    void removeClosedConnections() {
        logger.log(Level.TRACE, "removeClosedConnections start");
        try {
            lock.lock();
            if (!closed) {
                for (ProxyConnection connection : allConnections) {
                    try {
                        if (connection.isClosed()) {
                            allConnections.remove(connection);
                            idlingConnections.remove(connection);
                            logger.log(Level.DEBUG, "connection " + connection + " removed from the pool");
                            addConnection();
                        }
                    } catch (SQLException e) {
                        logger.log(Level.ERROR, "SQLException while checking if connection is closed", e);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
        logger.log(Level.TRACE, "removeClosedConnections end");
    }

    private void deregisterDrivers() {
        Enumeration<Driver> enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()){
            try {
                DriverManager.deregisterDriver(enumeration.nextElement());
            } catch (SQLException e) {
                logger.log(Level.ERROR, "could not deregister driver", e);
            }
        }
        logger.log(Level.INFO, "drivers deregistered");
    }
}
