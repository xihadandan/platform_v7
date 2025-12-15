package com.wellsoft.context.util.security;

import com.wellsoft.context.util.encode.EncodeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * 支持SHA-1/MD5消息摘要的工具类.
 * <p>
 * 支持Hex与Base64两种编码方式.
 *
 * @author lilin
 */
public class DigestUtils {

    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";

    // -- String Hash function --//

    /**
     * 对输入字符串进行sha1散列, 返回Hex编码的结果.
     *
     * @param input sha1散列
     * @return ex编码的结果
     */
    public static String sha1ToHex(String input) {
        byte[] digestResult = digest(input, SHA1);
        return EncodeUtils.hexEncode(digestResult);
    }

    /**
     * 对输入字符串进行sha1散列, 返回Base64编码的结果.
     *
     * @param input 进行sha1散列的字符串
     * @return Base64编码的结果
     */
    public static String sha1ToBase64(String input) {
        byte[] digestResult = digest(input, SHA1);
        return EncodeUtils.base64Encode(digestResult);
    }

    /**
     * 对输入字符串进行sha1散列, 返回Base64编码的URL安全的结果.
     *
     * @param input 进行sha1散列的字符串
     * @return Base64编码的URL安全的结果
     */
    public static String sha1ToBase64UrlSafe(String input) {
        byte[] digestResult = digest(input, SHA1);
        return EncodeUtils.base64UrlSafeEncode(digestResult);
    }

    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     *
     * @param input     进行sha1散列的字符串
     * @param algorithm 算法类型
     * @return 散列结果
     */
    private static byte[] digest(String input, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            return messageDigest.digest(input.getBytes());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Security exception", e);
        }
    }

    // -- File Hash function --//

    /**
     * 对文件进行md5散列,返回Hex编码结果.
     *
     * @param input 进行md5散列的文件
     * @return Hex编码结果
     * @throws IOException
     */
    public static String md5ToHex(InputStream input) throws IOException {
        return digest(input, MD5);
    }

    /**
     * 对文件进行sha1散列,返回Hex编码结果.
     *
     * @param input 进行sha1散列的文件
     * @return Hex编码结果
     * @throws IOException
     */
    public static String sha1ToHex(InputStream input) throws IOException {
        return digest(input, SHA1);
    }

    /**
     * 对文件进行散列, 支持md5与sha1算法.
     *
     * @param input     进行散列的文件
     * @param algorithm 算法
     * @return 散列结果
     * @throws IOException
     */
    private static String digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int bufferLength = 1024;
            byte[] buffer = new byte[bufferLength];
            int read = input.read(buffer, 0, bufferLength);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, bufferLength);
            }

            return EncodeUtils.hexEncode(messageDigest.digest());

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Security exception", e);
        }
    }
}
