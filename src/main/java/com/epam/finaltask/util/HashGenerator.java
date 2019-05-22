package com.epam.finaltask.util;

/**
 * Generator used to generate hashes based on specific algorithm with addition of salt.
 */
public interface HashGenerator {
    /**
     *  Generates hash using specified algorithm from data and salt.
     * @param data          the data to be hashed
     * @param salt          the salt to be used in hash generation
     * @param algorithm     the hashing algorithm
     * @return generated hash
     */
    String hash(String data, String salt, String algorithm);
}
