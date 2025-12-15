/*
 * @(#)2013-11-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.integration.security.ServerPasswordCallback;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-27.1	zhulh		2013-11-27		Create
 * </pre>
 * @date 2013-11-27
 */
public class MerlinCrypto {
    public static final String KEY_SIGN_PROP_FILENAME = "integration.ws.security.keyStorePropFile";
    public static final String KEY_KEYPASS_PROP_FILENAME = "integration.ws.security.keypassPropFile";
    private static MerlinCrypto merlinCrypto;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Crypto crypto;
    private Properties signProperties;
    private Properties keypassProperties;

    private MerlinCrypto(String signPropFilename, String keypassPropFilename) {
        signProperties = getProperties(signPropFilename);
        keypassProperties = getProperties(keypassPropFilename);
        try {
            crypto = CryptoFactory.getInstance(signProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static final String getKeyStorePropFile() {
        String signPropFilename = Config.getValue(KEY_SIGN_PROP_FILENAME);
        return signPropFilename = signPropFilename == null ? "ca/serverStore-SSGL.properties" : signPropFilename;
    }

    public static final String getKeypassPropFile() {
        String keypassPropFilename = Config.getValue(KEY_KEYPASS_PROP_FILENAME);
        return keypassPropFilename = keypassPropFilename == null ? "ca/keypass.properties" : keypassPropFilename;
    }

    public static MerlinCrypto getInstace() {
        if (merlinCrypto == null) {
            merlinCrypto = new MerlinCrypto(getKeyStorePropFile(), getKeypassPropFile());
        }
        return merlinCrypto;
    }

    public static void main(String[] args) throws Exception {
        MerlinCrypto cryto = MerlinCrypto.getInstace();
        // String[] aliases = cryto.getAliasesForDN("CN=client1");
        // for (String alias : aliases) {
        // System.out.println(alias);
        // }
        //
        // // 消息摘要
        // System.out.println(cryto.digestAsBase64("abc"));
        System.out.println(cryto.digestAsBase64(new File("F:\\11.doc")));
        //
        // // 数字签名
        // System.out.println(cryto.signAsBase64("abc"));
        // System.out.println(cryto.signAsBase64(new File("E:\\CA\\de.txt")));
        //
        // System.out.println(cryto.getDefaultX509Alias());
        // // 签名验证
        // String signedData =
        // "IAvfHgWqPB5ZoxRlFOLKgq24wcIDOWTYjjDIcPPaqbGxQaBBvqMkhgobRnM9XTQ3sSNpedAnme183ATomH261xnBnXj8Ae+v1jp9D5+58JdPCigZQTOQyNhqbdZUCHSIcMkJ5U55xHkoCi5RSRSqUc2xM2do4dhGuk7ACWWSnNA=";
        // System.out.println(cryto.verify("abc", signedData));
        // System.out.println(cryto.verify(new File("E:\\CA\\de.txt"),
        // signedData));

        // // 加解密
        // System.out.println(cryto.encrypt("abc"));
        // System.out.println(cryto.decrypt(cryto.encrypt("abc")));
        //
        // // 加解密
        // cryto.encrypt(new File("F:\\11.doc"), new
        // File("E:\\AppData\\tmp\\ec2b00c0-50a6-4e37-816c-1ad35e3a63123"));
        // cryto.decrypt(new
        // File("E:\\AppData\\tmp\\ec2b00c0-50a6-4e37-816c-1ad35e3a63123"), new
        // File(
        // "E:\\AppData\\tmp\\ec2b00c0-50a6-4e37-816c-1ad35e3a6312.doc"));
    }

    /**
     * @return the crypto
     */
    public Crypto getCrypto() {
        return crypto;
    }

    /**
     * @return the keypassProperties
     */
    public Properties getKeypassProperties() {
        return keypassProperties;
    }

    /**
     * @param signPropFilename
     * @return
     */
    private Properties getProperties(String signPropFilename) {
        Properties properties = new Properties();
        try {
            // 签名解密
            URL url = ClassLoaderUtils.getResource(signPropFilename, this.getClass());
            InputStream in = url.openStream();
            properties.load(in);
            in.close();
            return properties;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return properties;
    }

    /**
     * 获取服务器私钥
     *
     * @return
     */
    private PrivateKey getPrivate() throws Exception {
        String alias = crypto.getDefaultX509Identifier();
        String password = keypassProperties.getProperty(alias);
        return crypto.getPrivateKey(alias, password);
        // KeyStore ks = KeyStore.getInstance("JKS");
        // String path =
        // "D:\\well-soft\\workspace\\wellpt-web\\src\\main\\resources\\server-keystore.jks";
        // FileInputStream stream = new FileInputStream(path);
        // String storepass = "keyStorePassword";
        // String keypass = "serverPassword";
        // ks.load(stream, storepass.toCharArray());
        // PrivateKey privateKey = (PrivateKey) ks.getKey("server",
        // keypass.toCharArray());
        // stream.close();
        // return privateKey;
    }

    /**
     * 获取服务器证书
     *
     * @return
     */
    private Certificate getCertificate() throws Exception {
        // KeyStore ks = KeyStore.getInstance("JKS");
        // String path2 =
        // "D:\\well-soft\\workspace\\wellpt-integration\\src\\test\\resources\\client-truststore.jks";
        // FileInputStream is = new FileInputStream(path2);
        // String storepass2 = "keyStorePassword";
        // ks.load(is, storepass2.toCharArray());
        // return ks.getCertificate("server");
        String alias = crypto.getDefaultX509Identifier();
        // Certificate[] certificate = crypto
        // .getCertificates("63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        // System.out.println(((X509Certificate)
        // certificate[0]).getSubjectDN());
        CryptoType type = new CryptoType(CryptoType.TYPE.ALIAS);
        type.setAlias(alias);
        return crypto.getX509Certificates(type)[0];
    }

    /**
     * 根据证书主体，获取服务器证书
     *
     * @return
     */
    public Certificate getCertificate(String subjectDN) throws Exception {
        String[] alias = getAliasesForDN(subjectDN);
        CryptoType type = new CryptoType(CryptoType.TYPE.ALIAS);
        type.setAlias(alias[0]);
        return crypto.getX509Certificates(type)[0];
    }

    /**
     * 根据证书别名，获取服务器证书
     *
     * @return
     */
    public Certificate getCertificateByAlias(String alias) throws Exception {
        CryptoType type = new CryptoType(CryptoType.TYPE.ALIAS);
        type.setAlias(alias);
        return crypto.getX509Certificates(type)[0];
    }

    /**
     * 对文本生成消息摘要
     *
     * @param message
     * @return
     * @throws Exception
     */
    public byte[] digest(String message) throws Exception {
        return digest(new ByteArrayInputStream(message.getBytes("UTF-8")));
    }

    /**
     * 对文件生成消息摘要
     *
     * @param file
     * @return
     * @throws Exception
     */
    public byte[] digest(File file) throws Exception {
        MessageDigest m = MessageDigest.getInstance("SHA-1");
        FileInputStream fis = new FileInputStream(file);
        FileChannel ch = fis.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        m.update(byteBuffer);
        IOUtils.closeQuietly(fis);
        return m.digest();
        // return digest(new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * 对输入流生成消息摘要
     *
     * @param is
     * @return
     * @throws Exception
     */
    public byte[] digest(InputStream is) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        DigestInputStream din = new DigestInputStream(is, digest);
        // while (din.read() != -1) {
        // }
        int len = 1024;
        byte[] b = new byte[len];
        while (din.read(b, 0, len) != -1) {
        }
        byte digestData[] = digest.digest();

        return digestData;
    }

    /**
     * 对文本生成消息摘要，以base64编码返回
     *
     * @param message
     * @return
     * @throws Exception
     */
    public String digestAsBase64(String message) throws Exception {
        return digestAsBase64(new ByteArrayInputStream(message.getBytes("UTF-8")));
    }

    /**
     * 对文件生成消息摘要，以base64编码返回
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String digestAsBase64(File file) throws Exception {
        return digestAsBase64(new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * 对输入流生成消息摘要，以base64编码返回
     *
     * @param is
     * @return
     * @throws Exception
     */
    public String digestAsBase64(InputStream is) throws Exception {
        return Base64.encode(digest(is));
    }

    /**
     * 消息摘要生成签名
     *
     * @param digestData
     * @return
     * @throws Exception
     */
    private byte[] sign(byte[] digestData) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(getPrivate());
        signature.update(digestData);
        byte[] signedData = signature.sign();

        return signedData;
    }

    /**
     * 对文本进行数字签名
     *
     * @param message
     * @return
     * @throws Exception
     */
    public byte[] sign(String message) throws Exception {
        return sign(digest(message));
    }

    /**
     * 对文件进行数字签名
     *
     * @param file
     * @return
     * @throws Exception
     */
    public byte[] sign(File file) throws Exception {
        return sign(digest(file));
    }

    /**
     * 对输入流进行数字签名
     *
     * @param is
     * @return
     * @throws Exception
     */
    public byte[] sign(InputStream is) throws Exception {
        return sign(digest(is));
    }

    /**
     * 对文本进行数字签名，以base64编码返回
     *
     * @param message
     * @return
     * @throws Exception
     */
    public String signAsBase64(String message) throws Exception {
        return Base64.encode(sign(message));
    }

    /**
     * 对文件进行数字签名，以base64编码返回
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String signAsBase64(File file) throws Exception {
        return Base64.encode(sign(file));
    }

    /**
     * 对输入流进行数字签名，以base64编码返回
     *
     * @param is
     * @return
     * @throws Exception
     */
    public String signAsBase64(InputStream is) throws Exception {
        return Base64.encode(sign(is));
    }

    /**
     * 对消息摘要验证签名
     *
     * @param digestData
     * @param signedData
     * @return
     * @throws Exception
     */
    private boolean verify(byte[] digestData, byte[] signedData) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(getCertificate());
        signature.update(digestData);
        return signature.verify(signedData);
    }

    /**
     * 对文本验证数字签名，签名数据为base64编码数据
     *
     * @param message
     * @param signedData
     * @return
     * @throws Exception
     */
    public boolean verify(String message, String signedData) throws Exception {
        return verify(digest(message), Base64.decode(signedData));
    }

    /**
     * 对文件验证数字签名，签名数据为base64编码数据
     *
     * @param file
     * @param signedData
     * @return
     * @throws Exception
     */
    public boolean verify(File file, String signedData) throws Exception {
        return verify(digest(file), Base64.decode(signedData));
    }

    /**
     * 对输入流验证数字签名，签名数据为base64编码数据
     *
     * @param is
     * @param signedData
     * @return
     * @throws Exception
     */
    public boolean verify(InputStream is, String signedData) throws Exception {
        return verify(digest(is), Base64.decode(signedData));
    }

    /**
     * 对文件验证数字签名，签名数据为base64编码数据
     *
     * @param subjectDN
     * @param file
     * @param signedData
     * @return
     * @throws Exception
     */
    public boolean verify(String subjectDN, File file, String signedData) throws Exception {
        return verify(subjectDN, digest(file), Base64.decode(signedData));
    }

    /**
     * 对消息摘要验证签名
     *
     * @param subjectDN
     * @param digestData
     * @param signedData
     * @return
     * @throws Exception
     */
    private boolean verify(String subjectDN, byte[] digestData,
                           byte[] signedData) throws Exception {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(getCertificate(subjectDN));
        signature.update(digestData);
        return signature.verify(signedData);
    }

    /**
     * 服务器私钥加密消息，密文以base64编码形式返回
     *
     * @param message
     * @return
     * @throws Exception
     */
    public String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPrivate());
        byte[] plainText = message.getBytes("UTF-8");
        byte[] cipherData = cipher.doFinal(plainText);
        return Base64.encode(cipherData);
    }

    /**
     * 服务器私钥加密输入文件，密文输出到指定的输出文件
     *
     * @param input  明文输入文件
     * @param output 密文输出文件
     * @throws Exception
     */
    public void encrypt(File input, File output) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // RSAPrivateCrtKeyImpl key = (RSAPrivateCrtKeyImpl) getPrivate();
        cipher.init(Cipher.ENCRYPT_MODE, getPrivate());

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
        int len = -1;
        // 加密块最大长度限制
        // RSACryptoServiceProvider.KeySize / 8 - 11 = 1024 / 8 - 11 = 117
        byte[] b = new byte[117];
        while ((len = bis.read(b)) != -1) {
            bos.write(cipher.doFinal(b, 0, len));
        }
        bos.flush();

        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(bos);
        // bis.close();
        // bos.close();
    }

    /**
     * 服务器私钥解密，密文以base64编码形式传入
     *
     * @param cipherText
     * @return
     * @throws Exception
     */
    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getCertificate());
        byte[] cipherData = Base64.decode(cipherText);
        byte[] plainData = cipher.doFinal(cipherData);
        return new String(plainData, "UTF-8");
    }

    /**
     * 服务器私钥解密输入文件，明文输出到指定的输出文件
     *
     * @param input
     * @param output
     * @throws Exception
     */
    public void decrypt(File input, File output) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getCertificate());

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
        int len = -1;
        // 解密块最大长度限制
        // RSACryptoServiceProvider.KeySize / 8
        byte[] b = new byte[128];
        while ((len = bis.read(b)) != -1) {
            bos.write(cipher.doFinal(b, 0, len));
        }
        bos.flush();

        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(bos);
        // bis.close();
        // bos.close();
    }

    /**
     * 服务器私钥解密输入文件，明文输出到指定的输出文件
     *
     * @param input
     * @param output
     * @throws Exception
     */
    public void decrypt(String subjectDN, File input, File output) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getCertificate(subjectDN));

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
        int len = -1;
        // 解密块最大长度限制
        // RSACryptoServiceProvider.KeySize / 8
        byte[] b = new byte[128];
        while ((len = bis.read(b)) != -1) {
            bos.write(cipher.doFinal(b, 0, len));
        }
        bos.flush();

        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(bos);
        // bis.close();
        // bos.close();
    }

    public String getDefaultX509Alias() throws Exception {
        return crypto.getDefaultX509Identifier();
    }

    /**
     * 根据证书获取证书对应的别名
     *
     * @param cert
     * @return
     * @throws Exception
     */
    public String getAliasForX509Cert(X509Certificate cert) throws Exception {
        return crypto.getX509Identifier(cert);
    }

    /**
     * 根据证书主体获取证书对应的别名
     *
     * @param subjectDN
     * @return
     * @throws Exception
     */
    public String[] getAliasesForDN(String subjectDN) throws Exception {
        CryptoType type = new CryptoType(CryptoType.TYPE.SUBJECT_DN);
        type.setSubjectDN(subjectDN);
        X509Certificate[] certificates = crypto.getX509Certificates(type);
        String[] aliase = new String[certificates.length];
        for (int i = 0; i < certificates.length; i++) {
            aliase[i] = crypto.getX509Identifier(certificates[i]);
        }
        return aliase;
    }

    /**
     * @param cert
     * @throws WSSecurityException
     */
    public Map<String, Object> getWSS4JAttachmentPropsForDN(
            String subjectDN) throws Exception {
        Map<String, Object> props = new HashMap<String, Object>();
        String clientAlais = getAliasesForDN(subjectDN)[0];
        String serverAlais = crypto.getDefaultX509Identifier();
        String keyStorePropFile = MerlinCrypto.getKeyStorePropFile();
        props.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        props.put("encryptionUser", clientAlais);
        props.put("encryptionPropFile", keyStorePropFile);
        props.put("signatureUser", serverAlais);
        props.put("signaturePropFile", keyStorePropFile);
        return props;
    }

    /**
     * @param cert
     * @throws WSSecurityException
     */
    public Map<String, Object> getWSS4JAttachmentPropsForX509Cert(
            X509Certificate cert) throws WSSecurityException {
        Map<String, Object> props = new HashMap<String, Object>();
        String clientAlais = crypto.getX509Identifier(cert);
        String serverAlais = crypto.getDefaultX509Identifier();
        String keyStorePropFile = MerlinCrypto.getKeyStorePropFile();
        props.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        props.put("encryptionUser", clientAlais);
        props.put("encryptionPropFile", keyStorePropFile);
        props.put("signatureUser", serverAlais);
        props.put("signaturePropFile", keyStorePropFile);
        return props;
    }

}
