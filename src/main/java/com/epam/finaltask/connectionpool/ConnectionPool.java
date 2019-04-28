package com.epam.finaltask.connectionpool;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum ConnectionPool {
    instance();

    private final Logger logger;

    private int MAX_POOL_SIZE; //todo use or remove
    private PoolConfig config;
    private BlockingQueue<ProxyConnection> idlingConnections = new LinkedBlockingQueue<>();
    private Set<ProxyConnection> allConnections = ConcurrentHashMap.newKeySet();
    private Lock closingLock = new ReentrantLock();
    private Lock operationLock;
    private Lock cleaningLock;
    private boolean closed;

    ConnectionPool() {
        logger = LogManager.getLogger();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        operationLock = readWriteLock.readLock();
        cleaningLock = readWriteLock.writeLock();
    }

    public void init(PoolConfig poolConfig, int maintenancePeriod) {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            logger.log(Level.FATAL, "could not register database driver", e);
            throw new RuntimeException(e);
        }
        logger.log(Level.INFO, "database driver successfully registered");

        config = poolConfig;
        MAX_POOL_SIZE = config.getPoolSize();

        for (int i = 0; i < MAX_POOL_SIZE; i++) {
            addConnection();
        }

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new PoolSupervisor(), maintenancePeriod, maintenancePeriod);
    }

    public Connection getConnection() throws ConnectionPoolException {
        try {
            operationLock.lock();
            if (closed) {
                throw new ConnectionPoolException("Could not get connection: pool is closed");
            }
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
        }finally {
            operationLock.unlock();
        }
    }

    public void closePool() {
        try {
            closingLock.lock();
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
            closingLock.unlock();
        }
    }

    void releaseConnection(ProxyConnection connection) {
        try {
            operationLock.lock();
            if (allConnections.contains(connection)) {
                idlingConnections.put(connection);
                logger.log(Level.DEBUG, "connection " + connection + " released");
            }
        } catch (InterruptedException exception) {
            logger.log(Level.ERROR, "InterruptedException thrown while releasing connection", exception);
        } finally {
            operationLock.unlock();
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
            closingLock.lock();
            if (!closed) {
                try {
                    cleaningLock.lock();
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
                } finally {
                    cleaningLock.unlock();
                }
            }
        } finally {
            closingLock.unlock();
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
