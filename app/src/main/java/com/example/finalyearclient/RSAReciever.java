package com.example.finalyearclient;

import java.math.BigInteger;

public class RSAReciever {
    private final static BigInteger one      = new BigInteger("1");
    private final static BigInteger zero      = new BigInteger("0");
    private final static BigInteger two      = new BigInteger("2");

    private BigInteger publicKey; // e
    private BigInteger n;
    private BigInteger z;
    private BigInteger privateKey;

    RSAReciever() {
        BigInteger p = (two.pow(607)).subtract(one);//new BigInteger("5700734181645378434561188374130529072194886062117");//
        BigInteger q = (two.pow(521)).subtract(one);//new BigInteger("35894562752016259689151502540913447503526083241413");//
        z = (p.subtract(one)).multiply(q.subtract(one));
        n    = p.multiply(q);
        publicKey = new BigInteger("33445843524692047286771520482406772494816708076993");
        privateKey = publicKey.modInverse(z);

    }


    String decrypt(String encrypted) {
        BigInteger IntEncrypted = new BigInteger(encrypted);
        BigInteger IntDecrypted = IntEncrypted.modPow(privateKey, n);
        byte[] bytes = IntDecrypted.toByteArray();
        String decrypted = new String(bytes);
        return decrypted;
    }
}
