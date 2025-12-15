package com.wellsoft.context.util.sm;

import org.bouncycastle2.crypto.digests.SM3Digest;
import org.bouncycastle2.crypto.macs.HMac;
import org.bouncycastle2.crypto.params.KeyParameter;
import org.bouncycastle2.jce.provider.BouncyCastleProvider;
import org.bouncycastle2.pqc.math.linearalgebra.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Arrays;

/**
 * sm3加密，不可逆
 */
public class SM3Util {

    private final static String ENCODING = "utf-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * sm3算法加密
     *
     * @param content 待加密字符串
     * @return 返回加密后，固定长度=32的16进制字符串
     * @explain
     */
    public static String encrypt(String content) {
        String hex = "";
        try {
            // 将字符串转换成byte数组后，调用hash()
            byte[] hash = hash(content.getBytes(ENCODING));
            // 将返回的hash值转换成16进制字符串
            hex = ByteUtils.toHexString(hash);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hex;
    }

    /**
     * 返回长度=32的byte数组
     *
     * @param src
     * @return
     * @explain 生成对应的hash值
     */
    public static byte[] hash(byte[] src) {
        SM3Digest digest = new SM3Digest();
        digest.update(src, 0, src.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    /**
     * 判断源数据与加密数据是否一致
     *
     * @param src 原字符串
     * @param hex 16进制字符串
     * @return 校验结果
     * @explain 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    public static boolean verify(String src, String hex) {
        boolean flag = false;
        try {
            byte[] hash = hash(src.getBytes(ENCODING));
            if (Arrays.equals(hash, ByteUtils.fromHexString(hex))) {
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * sm3算法加密
     *
     * @param key     16进制密钥，忽略大小写
     * @param content 待加密字符串
     * @return 返回加密后，固定长度=32的16进制字符串
     * @explain
     */
    public static String encrypt(String key, String content) {
        String hex = "";
        try {
            // 将字符串转换成byte数组后，调用hmac()
            byte[] hash = hmac(ByteUtils.fromHexString(key), content.getBytes(ENCODING));
            // 将返回的hash值转换成16进制字符串
            hex = ByteUtils.toHexString(hash);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hex;
    }

    /**
     * 通过密钥进行加密
     *
     * @param key 密钥
     * @param src 被加密的byte数组
     * @return
     * @explain 指定密钥进行加密
     */
    public static byte[] hmac(byte[] key, byte[] src) {
        KeyParameter keyParameter = new KeyParameter(key);
        SM3Digest digest = new SM3Digest();
        HMac mac = new HMac(digest);
        mac.init(keyParameter);
        mac.update(src, 0, src.length);
        byte[] result = new byte[mac.getMacSize()];
        mac.doFinal(result, 0);
        return result;
    }

    /**
     * 判断源数据与加密数据是否一致
     *
     * @param key 16进制密钥，忽略大小写
     * @param src 原字符串
     * @param hex 16进制字符串
     * @return 校验结果
     * @explain 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    public static boolean verify(String key, String src, String hex) {
        boolean flag = false;
        try {
            byte[] hash = hmac(ByteUtils.fromHexString(key), src.getBytes(ENCODING));
            if (Arrays.equals(hash, ByteUtils.fromHexString(hex))) {
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void main(String[] args) throws Exception {
        String json = "0{admin}";

        String hex = SM3Util.encrypt(json);
        System.out.println(hex);

        System.out.println(SM3Util.verify("qwer1234{ADMIN}", hex));

        String key = "258714487abc";

        String hex2 = SM3Util.encrypt(key, json);

        System.out.println(hex2);

        System.out.println(SM3Util.verify(key, json, hex2));
    }
}
