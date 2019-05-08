package com.epam.finaltask.util;

public interface HashGenerator {

    String hash(String data, String salt, String algorithm);
}
