package com.epam.finaltask.util;

/**
 * Factory interface implementations of which should be used create instances of {@link HashGenerator}.
 */
public interface HashGeneratorFactory {

    /**
     * Creates {@link HashGenerator} implementation.
     * @return Implementation of {@link HashGenerator} interface
     */
    HashGenerator createHashGenerator();
}
