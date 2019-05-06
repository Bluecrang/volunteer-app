package com.epam.finaltask.connectionpool;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class PoolConfig {

    private static final Logger logger = LogManager.getLogger();

    private static final String DATABASE_URL_PROPERTY = "url";
    private static final String USERNAME_PROPERTY = "user";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String POOL_SIZE_PROPERTY = "size";

    private String databaseUrl;
    private String user;
    private String password;
    private int poolSize;

    public PoolConfig() {
    }

    public PoolConfig(String databaseUrl, String user, String password, Integer poolSize) {
        this.databaseUrl = databaseUrl;
        this.user = user;
        this.password = password;
        this.poolSize = poolSize;
    }

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
                    logger.log(Level.WARN, "pool size property is less than 1. default value will be used (" + poolSize + ")");
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARN, "pool size property is not an integer, default value will be used (" + poolSize + ")");
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
