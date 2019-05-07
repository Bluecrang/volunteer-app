package com.epam.finaltask.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HashGeneratorTest {

    HashGenerator hashGenerator = new HashGenerator();

    @DataProvider(name = "ValidParametersProvider")
    public Object[][] provideValidParameters() {
        return new Object[][] {
                {"data", "salt", "MD5"},
                {null, "car", "SHA-256"},
                {"something", null, "SHA-1"},
                {null, null, "MD5"}
        };
    }

    @Test(dataProvider = "ValidParametersProvider")
    public void hashTestValidParameters(String data, String salt, String algorithm) {
        hashGenerator.hash(data, salt, algorithm);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void hastTestNonexistentAlgorithm() {
        String data = "data";
        String salt = "salt";
        String algorithm = "Nonexistent algorithm";
        hashGenerator.hash(data, salt, algorithm);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void hastTestAlgorithmNull() {
        String data = "data";
        String salt = "salt";
        String algorithm = null;
        hashGenerator.hash(data, salt, algorithm);
    }
}
