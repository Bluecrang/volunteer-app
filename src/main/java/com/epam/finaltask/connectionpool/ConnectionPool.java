package com.epam.finaltask.connectionpool;

import com.epam.finaltask.validation.FileValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.Server;

import java.io.*;
import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Database connection pool. Requires call of the method {@link ConnectionPool#init(String, int)} before any interactions.
 */
public enum ConnectionPool {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger();

    private static Server server;

    /**
     * Object that contains pool configurations.
     */
    private PoolConfig config;

    /**
     * Queue that contains connection not currently in use.
     */
    private BlockingQueue<ProxyConnection> idlingConnections = new LinkedBlockingQueue<>();

    /**
     * Set that contains all connections of the connection pool.
     */
    private Set<ProxyConnection> allConnections = ConcurrentHashMap.newKeySet();

    /**
     * Lock that is used to prevent pool cleaning when closing it.
     */
    private Lock lock = new ReentrantLock();

    /**
     * Flag that shows if connection pool is closed.
     */
    private AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Initializes connection pool. Successful Invocation of this method is required before any interactions with pool.
     * Reads properties from file with chosen filename and creates {@link PoolConfig} using them.
     * Fills pool with maximal number of connections.
     * Schedules {@link PoolSupervisor} to clean pool from closed connections.
     * @param configFilename Filename of the connection pool config file
     * @param maintenancePeriod Period of pool cleansing in milliseconds
     */
    public void init(String configFilename, int maintenancePeriod) {
        try {
            server = Server.createTcpServer();
            server.start();
            logger.info("h2 server started");
            DriverManager.registerDriver(new org.h2.Driver());
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

        try (Connection connection =
                     DriverManager.getConnection(config.getDatabaseUrl(), config.getUser(), "")) {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/create_tables.sql")));
            String initQueries = bufferedReader.lines().collect(Collectors.joining());
            for (String query : initQueries.split("(?<=;)")) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(query);
                    Thread.sleep(50);
                }
            }
        } catch (Exception e) {
            String message = "could not create database tables";
            logger.log(Level.FATAL, message);
            throw new RuntimeException(e);
        }

        for (int i = 0; i < config.getPoolSize(); i++) {
            addConnection();
        }

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new PoolSupervisor(), maintenancePeriod, maintenancePeriod);
    }

    /**
     * Retrieves {@link ProxyConnection} from the pool. If there are no free connections, waits until connections frees.
     * @return {@link ProxyConnection} that provides access to {@link Connection}
     * @throws ConnectionPoolException If connection.isClosed() throws SQLException or thread is interrupted while taking connection
     */
    public Connection getConnection() throws ConnectionPoolException {
        if (closed.get()) {
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

    /**
     * Closes connection pool and sets it's {@link ConnectionPool#closed} flag to {@code} true.
     * Deregister drivers and closes all connections.
     */
    public void closePool() {
        try {
            lock.lock();
            closed.set(true);
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

    /**
     * Puts ProxyConnection to the pool.
     * @param connection Connection to put in the pool
     */
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

    /**
     * Opens new database connection and adds it to the pool.
     */
    void addConnection() {
        try {
            if (!closed.get()) {
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

    /**
     * Removes closed connections from the pool.
     */
    void removeClosedConnections() {
        logger.log(Level.TRACE, "removeClosedConnections start");
        try {
            lock.lock();
            if (!closed.get()) {
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

    /**
     * Deregister all drivers.
     */
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
        server.stop();
        logger.log(Level.INFO, "h2 server stopped");
    }
}
