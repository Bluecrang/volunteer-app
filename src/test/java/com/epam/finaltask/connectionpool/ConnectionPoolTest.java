package com.epam.finaltask.connectionpool;

import com.epam.finaltask.dao.impl.DatabaseTestUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.fail;

public class ConnectionPoolTest {

    @BeforeClass
    public void init() throws IOException, SQLException {
        DatabaseTestUtil.registerDrivers();
        DatabaseTestUtil.initializeDatabase();
        String configFilename = "src/test/resources/pool_test.properties";
        int maintenancePeriod = 1000 * 1000;
        ConnectionPool.INSTANCE.init(configFilename, maintenancePeriod);
    }

    @Test
    public void getConnectionTest() {
        try {
            Connection connection = ConnectionPool.INSTANCE.getConnection();
            Assert.assertTrue(connection instanceof ProxyConnection);

            connection.close();
        } catch (ConnectionPoolException e) {
            fail("Unexpected ConnectionPoolException", e);
        } catch (SQLException e) {
            fail("Unexpected SQLException", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() throws IOException, SQLException {
        ConnectionPool.INSTANCE.closePool();
        DatabaseTestUtil.registerDrivers();
        DatabaseTestUtil.dropSchema();
        DatabaseTestUtil.deregisterDrivers();
    }
}
