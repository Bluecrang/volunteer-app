package com.epam.finaltask.dao.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Enumeration;

public class DatabaseTestUtil {

    private static Connection connection;
    private static String urlAfterSchemaCreation;

    public static void initializeDatabase() throws SQLException, IOException {
        String[] script = String.join("",
                Files.readAllLines(Paths.get("src/test/resources/test_db_script.sql"), StandardCharsets.UTF_8)).split(";");
        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader("src/test/resources/db_settings.txt"))) {
            String urlBeforeSchemaCreation = bufferedReader.readLine();
            urlAfterSchemaCreation = bufferedReader.readLine();
            try (Connection connection = DriverManager
                    .getConnection(urlBeforeSchemaCreation)) {
                for (String query : script) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(query + ";");
                    }
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        connection = DriverManager.getConnection(urlAfterSchemaCreation);
        return connection;
    }

    public static void registerDrivers() throws SQLException {
        DriverManager.registerDriver(new org.h2.Driver());
    }

    public static void dropSchema() throws SQLException, IOException {
        try (Statement statement = getConnection().createStatement()) {
            String script = String.join("",
                    Files.readAllLines(Paths.get("src/test/resources/test_db_drop_schema.sql"), StandardCharsets.UTF_8));
            statement.execute(script);
        }
    }

    public static void deregisterDrivers() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
        Enumeration<Driver> enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()){
            DriverManager.deregisterDriver(enumeration.nextElement());
        }
    }
}
