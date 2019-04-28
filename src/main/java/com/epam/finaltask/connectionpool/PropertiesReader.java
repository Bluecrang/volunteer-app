package com.epam.finaltask.connectionpool;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static final Logger logger = LogManager.getLogger();

    public Properties readProperties(String filename) {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            properties.load(reader);
        } catch (FileNotFoundException e) {
            String message = "could not find properties";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        } catch (IOException e) {
            String message = "could not read properties";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        }
        return properties;
    }
}
