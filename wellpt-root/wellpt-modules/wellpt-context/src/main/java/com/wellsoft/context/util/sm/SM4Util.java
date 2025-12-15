package com.wellsoft.context.util.sm;

import org.bouncycastle2.jce.provider.BouncyCastleProvider;
import org.bouncycastle2.pqc.math.linearalgebra.ByteUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;

/**
 * sm4加密、解密与加密结果验证,可逆算法
 *
 * @author Administrator
 */
public class SM4Util {

    public static final String ALGORITHM_NAME = "SM4";
    // 加密算法/分组加密模式/分组填充方式
    // PKCS5Padding-以8个字节为一组进行分组加密
    // 定义分组加密模式使用：PKCS5Padding
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    // 128-32位16进制；256-64位16进制
    public static final int DEFAULT_KEY_SIZE = 128;
    private final static String ENCODING = "utf-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成ECB暗号
     *
     * @param algorithmName 算法名称
     * @param mode          模式
     * @param key
     * @return
     * @throws Exception
     * @explain ECB模式（电子密码本模式：Electronic codebook）
     */
    private static Cipher generateEcbCipher(String name, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(name, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    /**
     * sm4加密
     *
     * @param key     16进制密钥（忽略大小写）
     * @param content 待加密字符串
     * @return 返回16进制的加密字符串
     * @throws Exception
     * @explain 加密模式：ECB
     * 密文长度不固定，会随着被加密字符串长度的变化而变化
     */
    public static String encrypt(String key, String content) throws Exception {
        // 加密后的数组
        byte[] cipherArray = encrypt(ByteUtils.fromHexString(key), content.getBytes(ENCODING));
        return ByteUtils.toHexString(cipherArray);
    }

    /**
     * 加密模式之Ecb
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     * @explain
     */
    public static byte[] encrypt(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * sm4解密
     *
     * @param key 16进制密钥
     * @param hex 16进制的加密字符串（忽略大小写）
     * @return 解密后的字符串
     * @throws Exception
     * @explain 解密模式：采用ECB
     */
    public static String decrypt(String key, String hex) throws Exception {
        byte[] srcData = decrypt(ByteUtils.fromHexString(key), ByteUtils.fromHexString(hex));
        return new String(srcData, ENCODING);
    }

    /**
     * 解密
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     * @explain
     */
    public static byte[] decrypt(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 校验加密前后的字符串是否为同一数据
     *
     * @param key 16进制密钥（忽略大小写）
     * @param hex 16进制加密后的字符串
     * @param src 加密前的字符串
     * @return 是否为同一数据
     * @throws Exception
     * @explain
     */
    public static boolean verify(String key, String hex, String src) throws Exception {
        byte[] data = decrypt(ByteUtils.fromHexString(key), ByteUtils.fromHexString(hex));
        return Arrays.equals(data, src.getBytes(ENCODING));
    }

    public static void main(String[] args) throws Exception {
        String json = "{\"name\":\"Marydon\",\"website\":\"http://www.cnblogs.com/Marydon20170307\"}";
        String key = "86C63180C2806ED1F47B859DE501215B";
        String cipher = SM4Util.encrypt(key, json);
        System.out.println(cipher);

        System.out.println(SM4Util.verify(key, cipher, json));

        System.out.println(SM4Util.decrypt(key, cipher));

    }
}
