package com.epam.finaltask.util.impl;

import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;

/**
 * The implementation of {@link HashGeneratorFactory}. Should be used to create {@link HashGeneratorImpl} objects.
 */
public class HashGeneratorFactoryImpl implements HashGeneratorFactory {

    /**
     * Creates object of {@link HashGeneratorImpl} type.
     * @return the HashGeneratorImpl object
     */
    public HashGenerator createHashGenerator() {
        return new HashGeneratorImpl();
    }
}
