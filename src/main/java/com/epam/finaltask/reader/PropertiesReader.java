package com.epam.finaltask.reader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
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
