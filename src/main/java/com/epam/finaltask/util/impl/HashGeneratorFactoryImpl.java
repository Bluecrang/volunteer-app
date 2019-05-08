package com.epam.finaltask.util.impl;

import com.epam.finaltask.util.HashGenerator;
import com.epam.finaltask.util.HashGeneratorFactory;

public class HashGeneratorFactoryImpl implements HashGeneratorFactory {

    public HashGenerator createHashGenerator() {
        return new HashGeneratorImpl();
    }
}
