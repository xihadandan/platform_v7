/*
 * @(#)2019年5月24日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.license.support;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月24日.1	zhulh		2019年5月24日		Create
 * </pre>
 * @date 2019年5月24日
 */
public class DefaultLicenseManager implements LicenseManager {
    // 对称加密密钥对生成算法
    public static String ENCRYPTED_KEY_ALGORITHM = "AES";
    // 密码器对称加密密钥加密解密算法
    public static String ENCRYPTED_KEY_CIPHER_ALGORITHM = "AES/CBC/ISO10126Padding";

    public static String DSA = "DSA";
    public static String SHA1_WITH_DSA = "SHA1withDSA";
    public static String RSA = "RSA";
    public static String SHA1_WITH_RSA = "SHA1withRSA";

    private LicenseParam licenseParams;

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param licenseParams
     */
    public DefaultLicenseManager(LicenseParam licenseParams) {
        this.licenseParams = licenseParams;
    }

    /**
     * @param content
     * @param licenseFile
     */
    public void store(LicenseContent content, File licenseFile) {
        // 创建需可证书
        LicenseCertificate licenseCertificate = createLicenseCertificate(content);
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(licenseFile));
            writeCertificate(licenseCertificate, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 生成许可证
     *
     * @param content
     * @param outputStream
     */
    @Override
    public void store(LicenseContent content, OutputStream outputStream) {
        // 创建需可证书
        LicenseCertificate licenseCertificate = createLicenseCertificate(content);
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(outputStream);
            writeCertificate(licenseCertificate, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private void writeCertificate(LicenseCertificate licenseCertificate, DataOutputStream output) throws IOException {
        // AES密钥数据
        output.writeInt(licenseCertificate.getKeyEncoded().length);
        output.write(licenseCertificate.getKeyEncoded());
        // license内容加密数据
        output.writeInt(licenseCertificate.getCipherData().length);
        output.write(licenseCertificate.getCipherData());
        // license内容签名算法base64编码数据
        output.writeInt(licenseCertificate.getSignatureAlgorithmData().length);
        output.write(licenseCertificate.getSignatureAlgorithmData());
        // license内容签名数据
        output.writeInt(licenseCertificate.getSignedData().length);
        output.write(licenseCertificate.getSignedData());
        output.flush();
    }

    /**
     * @param licenseFile
     */
    public void install(File licenseFile) {
        InputStream input = null;
        try {
            input = new FileInputStream(licenseFile);
            byte[] licenseBytes = IOUtils.toByteArray(input);
            this.licenseParams.getPreferences().putByteArray("license", licenseBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     *
     */
    public String verify() {
        LicenseCertificate licenseCertificate = readLicenseCertificate();
        try {
            String licenseContent = decrypt(licenseCertificate);
            PublicKey publicKey = getPublicKey();
            String signatureAlgorithm = new String(Base64.decodeBase64(licenseCertificate.getSignatureAlgorithmData()));
            byte[] signature = licenseCertificate.getSignedData();
            boolean result = verify(licenseContent, signature, signatureAlgorithm, publicKey);
            if (!result) {
                throw new RuntimeException("verify error!");
            }
            return licenseContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 签名
     *
     * @param content
     * @return
     */
    @Override
    public String sign(String content) {
        String signature = "";
        try {
            // 签名数据
            PrivateKey privateKey = getPrivateKey();
            byte[] signedData = sign(content, privateKey);
            signature = new String(java.util.Base64.getEncoder().encode(signedData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * @param licenseContent
     * @return
     */
    private LicenseCertificate createLicenseCertificate(LicenseContent licenseContent) {
        LicenseCertificate licenseCertificate = new LicenseCertificate();
        FileInputStream input = null;
        try {
            if (licenseContent.getSubject() == null) {
                licenseContent.setSubject(licenseParams.getSubject());
            }
            // 密钥
            byte[] keyEncoded = getKeyEncoded(licenseContent.getSubject());

            // 创建密码器
            Cipher cipher = Cipher.getInstance(ENCRYPTED_KEY_CIPHER_ALGORITHM);

            // 初始化为加密模式的密码器
            SecretKeySpec key = new SecretKeySpec(keyEncoded, ENCRYPTED_KEY_ALGORITHM);

            // 初始化向量
            int ivLen = cipher.getBlockSize();
            byte[] ivBytes = new byte[ivLen];
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            // 加密
            String licenseJson = object2Json(licenseContent);
            byte[] cipherData = cipher.doFinal(licenseJson.getBytes());

            // 签名数据
            PrivateKey privateKey = getPrivateKey();
            String signatureAlgorithm = getSignatureAlgorithm(privateKey);
            byte[] signedData = sign(licenseJson, privateKey);

            licenseCertificate.setKeyEncoded(keyEncoded);
            licenseCertificate.setCipherData(cipherData);
            licenseCertificate.setSignatureAlgorithmData(Base64.encodeBase64(signatureAlgorithm.getBytes()));
            licenseCertificate.setSignedData(signedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return licenseCertificate;
    }

    /**
     * @return
     */
    private LicenseCertificate readLicenseCertificate() {
        LicenseCertificate licenseCertificate = new LicenseCertificate();
        DataInputStream input = null;
        try {
            byte[] licenseData = this.licenseParams.getPreferences().getByteArray("license", null);
            input = new DataInputStream(new ByteArrayInputStream(licenseData));
            // AES密钥数据
            int keyLength = input.readInt();
            byte[] keyEncoded = new byte[keyLength];
            input.readFully(keyEncoded);
            // license内容加密数据
            int cipherDataLength = input.readInt();
            byte[] cipherData = new byte[cipherDataLength];
            input.readFully(cipherData);
            // license内容签名算法base64编码数据
            int signatureAlgorithmLength = input.readInt();
            byte[] signatureAlgorithmData = new byte[signatureAlgorithmLength];
            input.readFully(signatureAlgorithmData);
            // license内容签名数据
            int signatureLength = input.readInt();
            byte[] signedData = new byte[signatureLength];
            input.readFully(signedData);

            licenseCertificate.setKeyEncoded(keyEncoded);
            licenseCertificate.setCipherData(cipherData);
            licenseCertificate.setSignatureAlgorithmData(signatureAlgorithmData);
            licenseCertificate.setSignedData(signedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return licenseCertificate;
    }

    /**
     * @return
     */
    private PrivateKey getPrivateKey() {
        PrivateKey privateKey = null;
        InputStream input = null;
        try {
            KeyStoreParam keyStoreParam = licenseParams.getKeyStoreParam();
            // JKS密钥库路径
            String jksPath = keyStoreParam.getKeyStorePath();
            // JKS密钥库密码
            String jksPassword = keyStoreParam.getKeyStorePassword();
            // 私钥别名
            String certAlias = keyStoreParam.getKeyAlias();
            // 私钥密码
            String certPassword = keyStoreParam.getKeyPassword();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            URL url = keyStoreParam.getClass().getResource(jksPath);
            input = url.openStream();
            keyStore.load(input, jksPassword.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(certAlias, certPassword.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return privateKey;
    }

    /**
     * @return
     */
    private PublicKey getPublicKey() {
        PublicKey publicKey = null;
        InputStream input = null;
        try {
            KeyStoreParam keyStoreParam = licenseParams.getKeyStoreParam();
            // JKS密钥库路径
            String jksPath = keyStoreParam.getKeyStorePath();
            // JKS密钥库密码
            String jksPassword = keyStoreParam.getKeyStorePassword();
            // 公钥别名
            String certAlias = keyStoreParam.getKeyAlias();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            URL url = keyStoreParam.getClass().getResource(jksPath);
            input = url.openStream();
            keyStore.load(input, jksPassword.toCharArray());
            publicKey = keyStore.getCertificate(certAlias).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return publicKey;
    }

    /**
     * @param licenseCertificate
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private String decrypt(LicenseCertificate licenseCertificate) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        // AES专用密钥
        SecretKeySpec key = new SecretKeySpec(licenseCertificate.getKeyEncoded(), ENCRYPTED_KEY_ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(ENCRYPTED_KEY_CIPHER_ALGORITHM);
        // 初始化向量
        int ivLen = cipher.getBlockSize();
        byte[] ivBytes = new byte[ivLen];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        // 初始化为加密模式的密码器
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        // 解密
        byte[] result = cipher.doFinal(licenseCertificate.getCipherData());
        String licenseContent = new String(result);
        return licenseContent;
    }

    /**
     * @param privateKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private static byte[] sign(String licenseContent, PrivateKey privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        // 签名类型
        Signature signature = Signature.getInstance(getSignatureAlgorithm(privateKey));
        signature.initSign(privateKey);
        signature.update(licenseContent.getBytes());
        return signature.sign();
    }

    /**
     * @param licenseContent
     * @param signatureData
     * @param signatureAlgorithm
     * @param publicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private boolean verify(String licenseContent, byte[] signatureData, String signatureAlgorithm, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initVerify(publicKey);
        signature.update(licenseContent.getBytes());
        boolean isVerify = signature.verify(signatureData);
        return isVerify;
    }

    /**
     * @param key
     * @return
     */
    private static String getSignatureAlgorithm(Key key) {
        String algorithm = key.getAlgorithm();
        if (DSA.equals(algorithm)) {
            return SHA1_WITH_DSA;
        } else {
            return SHA1_WITH_RSA;
        }
    }

    /**
     * 对象转化为json字符串
     * h
     *
     * @param object 转换对象
     * @return json字符串
     */
    public static String object2Json(Object object) {
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    /**
     * @return
     * @throws NoSuchAlgorithmException
     */
    protected byte[] getKeyEncoded(String password) throws Exception {
        // 创建AES的Key生产者
        KeyGenerator kgen = KeyGenerator.getInstance(ENCRYPTED_KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        // 128位的key生产者
        kgen.init(128, random);

        // 根据用户密码，生成一个密钥
        SecretKey secretKey = kgen.generateKey();
        // 返回基本编码格式的密钥，如果此密钥不支持编码，则返回
        byte[] enCodeFormat = secretKey.getEncoded();
        return enCodeFormat;
    }

}
