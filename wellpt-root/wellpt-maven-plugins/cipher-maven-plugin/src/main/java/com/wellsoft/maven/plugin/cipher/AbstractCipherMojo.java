/*
 * @(#)2019年5月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.maven.plugin.cipher;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月16日.1	zhulh		2019年5月16日		Create
 * </pre>
 * @date 2019年5月16日
 */
public abstract class AbstractCipherMojo extends AbstractMojo {

    // 对称加密密钥对生成算法
    public static String ENCRYPTED_KEY_ALGORITHM = "AES";
    // 密码器对称加密密钥加密解密算法
    public static String ENCRYPTED_KEY_CIPHER_ALGORITHM = "AES/CBC/ISO10126Padding";

    private byte[] keyEncoded;

    /**
     * @param bt1
     * @param bt2
     * @return
     */
    public static byte[] mergeBytes(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * @return
     * @throws NoSuchAlgorithmException
     */
    protected byte[] getKeyEncoded(String artifactId) throws Exception {
        if (keyEncoded != null) {
            return keyEncoded;
        }
        // 创建AES的Key生产者
        KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPTED_KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(artifactId.getBytes());
        // 128位的key生产者
        kgen.init(128, random);

        // 根据用户密码，生成一个密钥
        SecretKey secretKey = kgen.generateKey();
        // 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
        byte[] enCodeFormat = secretKey.getEncoded();
        keyEncoded = enCodeFormat;
        return enCodeFormat;
    }

}
