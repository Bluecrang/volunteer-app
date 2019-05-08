package com.epam.finaltask.util.impl;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SaltGenerator {

    private static final int HEX_RADIX = 16;
    private static final int NUMBER_OF_BITS = 128;

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(NUMBER_OF_BITS, random).toString(HEX_RADIX);
    }
}
