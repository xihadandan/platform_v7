/*
 * @(#)2014-1-22 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.aegis.type.mtom.StreamDataSource;
import org.apache.cxf.attachment.AttachmentDataSource;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.callback.CallbackHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-22.1	zhulh		2014-1-22		Create
 * </pre>
 * @date 2014-1-22
 */
public class WSS4JOutAttachment {
    // 对称加密密钥对生成算法
    public static final String ENCRYPTED_KEY_ALGORITHM = "AES";
    // 对称加密密钥加密解密算法
    public static final String ENCRYPTED_KEY_ALGORITHM_CIPHER_ALGORITHM = "AES/CBC/ISO10126Padding";
    // 服务器证书公钥加密算法
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    // 消息摘要生成算法
    public static final String DIGEST_ALGORITHM = "SHA-1";
    // 服务器证书私钥签名算法
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static Map<String, Crypto> cryptoMap = new HashMap<String, Crypto>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String encryptedKey;
    private String digestValue;
    private String signatureValue;
    private File originalFile;
    private String contentType;
    private CachedOutputStream cache;
    private String encryptionUser;
    private String signatureUser;
    private String sigPropFile;
    private String encPropFile;
    private StreamingData streamingData;
    private AttachmentDataSource attachmentDataSource;
    private CallbackHandler callbackHandler;

    public WSS4JOutAttachment(StreamingData streamingData,
                              Map<String, Object> props) throws Exception {
        this.streamingData = streamingData;
        this.contentType = streamingData.getDataHandler().getContentType();
        attachmentDataSource = new AttachmentDataSource(this.contentType,
                streamingData.getDataHandler()
                        .getInputStream());
        //attachmentDataSource.hold();
        init(props);
    }

    public WSS4JOutAttachment(File file, Map<String, Object> props) throws Exception {
        this.streamingData = new StreamingData();
        this.originalFile = file;
        this.streamingData.setFileName(file.getName());
        this.contentType = new MimetypesFileTypeMap().getContentType(file);
        attachmentDataSource = new AttachmentDataSource(this.contentType,
                new FileInputStream(file));
        //attachmentDataSource.hold();
        init(props);
    }

    /**
     * @param streamingData2
     * @param props
     */
    private void init(Map<String, Object> props) throws Exception {
        try {
            this.encryptionUser = (String) props.get(WSHandlerConstants.ENCRYPTION_USER);
            this.signatureUser = (String) props.get(WSHandlerConstants.SIGNATURE_USER);
            this.callbackHandler = (CallbackHandler) Class.forName(
                    (String) props.get(WSHandlerConstants.PW_CALLBACK_CLASS)).newInstance();
            this.encPropFile = (String) props.get(WSHandlerConstants.ENC_PROP_FILE);
            if (!cryptoMap.containsKey(this.encPropFile)) {
                cryptoMap.put(this.encPropFile, CryptoFactory.getInstance(encPropFile));
            }
            this.sigPropFile = (String) props.get(WSHandlerConstants.SIG_PROP_FILE);
            if (!cryptoMap.containsKey(this.sigPropFile)) {
                cryptoMap.put(this.sigPropFile, CryptoFactory.getInstance(this.sigPropFile));
            }
        } catch (Exception e) {
            this.release();

            logger.info(e.getMessage());
            throw e;
        }
    }

    public void signAndEncrypt() throws Exception {
        try {
            // 1 生成加密附件的随机密钥对
            KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPTED_KEY_ALGORITHM);
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            Cipher encryCipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            int ivLen = encryCipher.getBlockSize();
            byte[] ivBytes = new byte[ivLen];
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            encryCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            // 2对文件加密
            cache = new CachedOutputStream();
            cache.holdTempFile();
            InputStream input = attachmentDataSource.getInputStream();
            CipherOutputStream output = new CipherOutputStream(cache, encryCipher);
            IOUtils.copyLarge(input, output);
            output.flush();
            output.close();
            input.close();
            input = null;
            cache.lockOutputStream();

            // 3 用服务器公钥对密钥对加密
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            Crypto encryptionCrypto = cryptoMap.get(this.encPropFile);
            CryptoType aliasType = new CryptoType(CryptoType.TYPE.ALIAS);
            aliasType.setAlias(encryptionUser);
            cipher.init(Cipher.ENCRYPT_MODE,
                    encryptionCrypto.getX509Certificates(aliasType)[0]);
            byte[] keyEncoded = secretKey.getEncoded();
            byte[] encryptedKeyData = cipher.doFinal(keyEncoded);
            encryptedKey = Base64.encode(encryptedKeyData);

            // 4 用私钥对文件进行签名
            // 生成文件消息摘要
            byte[] digestData = null;
            if (originalFile != null) {
                digestData = digest(this.originalFile);
            } else {
                digestData = digest(attachmentDataSource.getInputStream());
            }
            this.digestValue = Base64.encode(digestData);
            WSPasswordCallback callback = new WSPasswordCallback(signatureUser,
                    WSPasswordCallback.SIGNATURE);
            WSPasswordCallback[] callbacks = new WSPasswordCallback[]{callback};
            callbackHandler.handle(callbacks);
            Crypto signatureCrypto = cryptoMap.get(this.sigPropFile);
            PrivateKey privateKey = signatureCrypto.getPrivateKey(signatureUser,
                    callback.getPassword());
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(digestData);
            byte[] signatureData = signature.sign();
            this.signatureValue = Base64.encode(signatureData);
            attachmentDataSource.release();
        } catch (Exception e) {
            this.release();

            logger.info(e.getMessage());
            throw e;
        }
    }

    /**
     * @param inputStream
     * @return
     * @throws Exception
     */
    private byte[] digest(InputStream inputStream) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        DigestInputStream din = new DigestInputStream(inputStream, digest);
        int len = 1024;
        byte[] b = new byte[len];
        while (din.read(b, 0, len) != -1) {
        }
        byte digestData[] = digest.digest();

        return digestData;
    }

    /**
     * 对文件生成消息摘要
     *
     * @param inputFile
     * @return
     * @throws Exception
     */
    private byte[] digest(File inputFile) throws Exception {
        MessageDigest m = MessageDigest.getInstance(DIGEST_ALGORITHM);
        FileInputStream fis = new FileInputStream(inputFile);
        FileChannel ch = fis.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, inputFile.length());
        m.update(byteBuffer);
        IOUtils.closeQuietly(fis);
        return m.digest();
    }

    /**
     *
     */
    private void release() {
        if (attachmentDataSource != null) {
            attachmentDataSource.release();
        }
        if (cache != null) {
            cache.releaseTempFileHold();
        }
    }

    /**
     * 获取加密后的输入流
     *
     * @return
     * @throws Exception
     */
    public InputStream getEncryptedStream() throws Exception {
        return cache.getInputStream();
    }

    /**
     * @return the encryptedKey
     */
    public String getEncryptedKey() {
        return encryptedKey;
    }

    /**
     * @return the digestValue
     */
    public String getDigestValue() {
        return digestValue;
    }

    /**
     * @return the signatureValue
     */
    public String getSignatureValue() {
        return signatureValue;
    }

    /**
     * 获取服务器证书的base64编码
     *
     * @return
     * @throws Exception
     */
    public String getCertificate() throws Exception {
        Crypto encryptionCrypto = cryptoMap.get(this.encPropFile);
        CryptoType aliasType = new CryptoType(CryptoType.TYPE.ALIAS);
        aliasType.setAlias(encryptionUser);
        return Base64.encode(encryptionCrypto.getX509Certificates(aliasType)[0].getEncoded());
    }

    /**
     * 获取加密等处理操作后的StreamingData
     *
     * @return
     * @throws Exception
     */
    public StreamingData getEncryptedStreamingData() throws Exception {
        try {
            streamingData.setEncryptedKeyAlgorithm(ENCRYPTED_KEY_ALGORITHM);
            streamingData.setEncryptedKeyCipherAlgorithm(ENCRYPTED_KEY_ALGORITHM_CIPHER_ALGORITHM);
            streamingData.setCipherAlgorithm(CIPHER_ALGORITHM);
            streamingData.setDigestAlgorithm(DIGEST_ALGORITHM);
            streamingData.setSignatureAlgorithm(SIGNATURE_ALGORITHM);

            streamingData.setEncryptedKey(encryptedKey);
            streamingData.setDigestValue(digestValue);
            streamingData.setSignatureValue(signatureValue);
            streamingData
                    .setDataHandler(new DataHandler(
                            new StreamDataSource(this.contentType, cache.getInputStream())));
        } catch (Exception e) {
            this.release();

            logger.info(e.getMessage());
            throw e;
        }
        this.release();
        return streamingData;
    }

}
