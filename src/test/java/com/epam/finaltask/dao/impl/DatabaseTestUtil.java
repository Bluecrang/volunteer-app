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

    public static synchronized Connection initiateDatabaseAndGetConnection() throws SQLException, IOException {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        String[] script = String.join("",
                Files.readAllLines(Paths.get("src/test/resources/test_db_script.sql"), StandardCharsets.UTF_8)).split(";");
        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader("src/test/resources/db_settings.txt"))) {
            String urlBeforeSchemaCreation = bufferedReader.readLine();
            String urlAfterSchemaCreation = bufferedReader.readLine();
            try (Connection connection = DriverManager
                    .getConnection(urlBeforeSchemaCreation)) {
                for (String query : script) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(query + ";");
                    }
                }
            }
            connection = DriverManager.getConnection(urlAfterSchemaCreation);
        }
        return  connection;
    }

    public static void deregisterDrivers() throws SQLException, IOException {
        try (Statement statement = connection.createStatement()) {
            String script = String.join("",
                    Files.readAllLines(Paths.get("src/test/resources/test_db_drop_schema.sql"), StandardCharsets.UTF_8));
            statement.execute(script);
        }
        connection.close();
        Enumeration<Driver> enumeration = DriverManager.getDrivers();
        while (enumeration.hasMoreElements()){
            DriverManager.deregisterDriver(enumeration.nextElement());
        }
    }
}
