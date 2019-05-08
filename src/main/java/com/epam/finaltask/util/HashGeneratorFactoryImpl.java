package com.epam.finaltask.util;

public class HashGeneratorFactoryImpl implements HashGeneratorFactory {

    public HashGenerator createHashGenerator() {
        return new HashGeneratorImpl();
    }
}
