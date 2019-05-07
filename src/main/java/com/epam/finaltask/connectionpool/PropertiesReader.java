package com.epam.finaltask.connectionpool;

import com.epam.finaltask.validation.FileValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

class PropertiesReader {

    private static final Logger logger = LogManager.getLogger();

    Properties readProperties(Reader reader) {
        if (reader == null) {
            String message = "reader is null";
            logger.log(Level.FATAL, message);
            throw new RuntimeException(message);
        }
        Properties properties = new Properties();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            properties.load(bufferedReader);
        } catch (IOException e) {
            String message = "could not read properties";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        }
        return properties;
    }

    Properties readProperties(String filename) {
        FileValidator fileValidator = new FileValidator();
        if (!fileValidator.validate(filename)) {
            String message = "properties filename is invalid";
            logger.log(Level.FATAL, message);
            throw new RuntimeException(message);
        }
        try (FileReader fileReader = new FileReader(filename)) {
            return readProperties(fileReader);
        } catch (FileNotFoundException e) {
            String message = "could not find file";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        } catch (IOException e) {
            String message = "IOException while working with FileReader";
            logger.log(Level.FATAL, message, e);
            throw new RuntimeException(message, e);
        }
    }
}
