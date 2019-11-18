package main.webapp.java;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyGenerator {

    private static volatile SecretKey sk = null;

    public static SecretKey getPrivateKey() {
        if (sk == null) {
            synchronized (KeyGenerator.class) {
                if (sk == null) {
                    try {
                        javax.crypto.KeyGenerator kg = javax.crypto.KeyGenerator.getInstance("HmacSHA256");
                        sk = kg.generateKey();
                        return sk;
                    } catch (NoSuchAlgorithmException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException("Couldn't find the specified algorithm");
                    }
                } else {
                    return sk;
                }
            }
        } else {
            return sk;
        }
    }

    public static SecretKey getNewSecretKey() throws NoSuchAlgorithmException {
        javax.crypto.KeyGenerator kg = javax.crypto.KeyGenerator.getInstance("HmacSHA256");
        return kg.generateKey();
    }
}
