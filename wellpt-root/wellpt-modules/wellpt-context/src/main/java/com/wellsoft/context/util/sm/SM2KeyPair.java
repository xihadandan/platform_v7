package com.wellsoft.context.util.sm;

import org.bouncycastle2.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * SM2公私钥实体类
 */
public class SM2KeyPair {

    /**
     * 公钥
     */
    private ECPoint publicKey;

    /**
     * 私钥
     */
    private BigInteger privateKey;

    SM2KeyPair(ECPoint publicKey, BigInteger privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

}
