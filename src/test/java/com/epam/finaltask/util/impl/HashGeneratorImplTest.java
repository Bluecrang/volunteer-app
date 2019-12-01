package com.epam.finaltask.util.impl;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HashGeneratorImplTest {

    private HashGeneratorImpl hashGeneratorImpl = new HashGeneratorImpl();

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
    public void hash_ValidParameters_hash(String data, String salt, String algorithm) {
        String actual = hashGeneratorImpl.hash(data, salt, algorithm);;

        Assert.assertNotNull(actual);
    }

    @Test
    public void hast_nonexistentAlgorithm_runtimeException() {
        String data = "data";
        String salt = "salt";
        String algorithm = "Nonexistent algorithm";

        Assert.assertThrows(RuntimeException.class, () -> {
            hashGeneratorImpl.hash(data, salt, algorithm);
        });
    }

    @Test
    public void hast_algorithmNull_runtimeException() {
        String data = "data";
        String salt = "salt";
        String algorithm = null;

        Assert.assertThrows(RuntimeException.class, () -> {
            hashGeneratorImpl.hash(data, salt, algorithm);
        });
    }
}
