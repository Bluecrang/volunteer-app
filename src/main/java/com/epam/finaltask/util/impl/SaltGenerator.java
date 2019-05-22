package com.epam.finaltask.util.impl;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * The SaltGenerator class provides method to generate random salt using {@link SecureRandom}.
 */
public class SaltGenerator {

    private static final int HEX_RADIX = 16;
    /**
     * Number of random bits generated
     */
    private static final int NUMBER_OF_BITS = 128;

    /**
     * Generates hexadecimal salt using {@link SecureRandom}. {@link #NUMBER_OF_BITS} defines how many random bits
     * will be generated.
     * @return the hexadecimal salt string
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(NUMBER_OF_BITS, random).toString(HEX_RADIX);
    }
}
