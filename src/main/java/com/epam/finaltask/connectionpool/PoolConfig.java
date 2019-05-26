package com.epam.finaltask.connectionpool;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Class instances of that contain pool configurations.
 */
public class PoolConfig {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Default size of the connection pool
     */
    private static final int DEFAULT_POOL_SIZE = 5;

    private static final String DATABASE_URL_PROPERTY = "url";
    private static final String USERNAME_PROPERTY = "user";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String POOL_SIZE_PROPERTY = "size";

    /**
     * Url of the database.
     */
    private String databaseUrl;

    /**
     * Name of the database user.
     */
    private String user;

    /**
     * Password of the database user.
     */
    private String password;

    /**
     * Size of the connection pool.
     */
    private int poolSize = DEFAULT_POOL_SIZE;

    public PoolConfig() {
    }

    /**
     * Creates PoolConfig with values taken from the properties.
     * Sets default pool size is less than 1.
     * @param properties Properties that provide necessary data to the pool config
     */
    public PoolConfig(Properties properties) {
        databaseUrl = properties.getProperty(DATABASE_URL_PROPERTY);
        user = properties.getProperty(USERNAME_PROPERTY);
        password = properties.getProperty(PASSWORD_PROPERTY);
        String poolSizeString = properties.getProperty(POOL_SIZE_PROPERTY);
        if (poolSizeString != null) {
            try {
                int poolSizeInteger = Integer.valueOf(poolSizeString);
                if (poolSizeInteger > 0) {
                    poolSize = poolSizeInteger;
                } else {
                    logger.log(Level.WARN, "pool size property is less than 1. default value will be used (" + DEFAULT_POOL_SIZE + ")");
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARN, "pool size property is not an integer, default value will be used (" + DEFAULT_POOL_SIZE + ")");
            }
        }
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PoolConfig{");
        sb.append("databaseUrl='").append(databaseUrl).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", poolSize=").append(poolSize);
        sb.append('}');
        return sb.toString();
    }
}
