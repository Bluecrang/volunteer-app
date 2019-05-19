package com.epam.finaltask.util.impl;

import com.epam.finaltask.connectionpool.PropertiesReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import static org.testng.Assert.fail;

public class PropertiesReaderTest {

    private PropertiesReader propertiesReader = new PropertiesReader();

    @DataProvider(name = "ValidPropertiesProvider")
    public Object[][] provideProperties() {
        Properties properties1 = new Properties();
        properties1.setProperty("prop1", "value1");
        properties1.setProperty("prop2", "value2");
        properties1.setProperty("prop3", "value3");
        return new Object[][] {
                {properties1},
                {new Properties()}
        };
    }

    @Test(dataProvider = "ValidPropertiesProvider")
    public void readPropertiesTestValid(Properties expected) {
        StringWriter writer = new StringWriter();
        try {
            expected.store(writer, "");
            Reader reader = new StringReader(writer.toString());

            Properties actual = propertiesReader.readProperties(reader);

            Assert.assertEquals(actual, expected);
        } catch (IOException e) {
            fail("unexpected IOException");
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void readPropertiesTestReaderNull() {
        Reader reader = null;
        propertiesReader.readProperties(reader);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void readPropertiesTestFilenameNull() {
        String filename = null;
        propertiesReader.readProperties(filename);
    }
}
