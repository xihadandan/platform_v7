/*
 * @(#)2014-1-22 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.aegis.type.mtom.StreamDataSource;
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
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.io.InputStream;
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
public class WSS4JIntAttachment {

    private static Map<String, Crypto> cryptoMap = new HashMap<String, Crypto>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private CachedOutputStream cache;
    private String encryptionUser;
    private String signatureUser;
    private String sigPropFile;
    private String encPropFile;
    private StreamingData streamingData;

    private CallbackHandler callbackHandler;

    private boolean verifyValue;

    public WSS4JIntAttachment(StreamingData streamingData,
                              Map<String, Object> props) throws Exception {
        try {
            this.streamingData = streamingData;
            encryptionUser = (String) props.get(WSHandlerConstants.ENCRYPTION_USER);
            signatureUser = (String) props.get(WSHandlerConstants.SIGNATURE_USER);
            callbackHandler = (CallbackHandler) Class.forName(
                    (String) props.get(WSHandlerConstants.PW_CALLBACK_CLASS))
                    .newInstance();
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

    public void verifyAndDecrypt() throws Exception {
        try {
            // 1 用私钥解密对称密钥
            String encryptedKey = this.streamingData.getEncryptedKey();
            // 服务器证书私钥解密算法	RSA/ECB/PKCS1Padding
            String cipherAlgorithm = this.streamingData.getCipherAlgorithm();
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            WSPasswordCallback callback = new WSPasswordCallback(signatureUser,
                    WSPasswordCallback.SIGNATURE);
            WSPasswordCallback[] callbacks = new WSPasswordCallback[]{callback};
            callbackHandler.handle(callbacks);
            Crypto signatureCrypto = cryptoMap.get(this.sigPropFile);
            PrivateKey privateKey = signatureCrypto.getPrivateKey(signatureUser,
                    callback.getPassword());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] secretKeyEncode = cipher.doFinal(Base64.decode(encryptedKey));

            // 2 用解密出来的对称密钥对文件流进行解密
            byte[] digestData = Base64.decode(this.streamingData.getDigestValue());
            // 对称加密密钥对生成算法	AES
            String encryptedKeyAlgorithm = this.streamingData.getEncryptedKeyAlgorithm();
            // 对称加密密钥加密解密算法	AES/CBC/ISO10126Padding
            String encryptedKeyCipherAlgorithm = this.streamingData.getEncryptedKeyCipherAlgorithm();
            SecretKey secretKey = new SecretKeySpec(secretKeyEncode, encryptedKeyAlgorithm);
            Cipher decryptCipher = Cipher.getInstance(encryptedKeyCipherAlgorithm);
            int ivLen = decryptCipher.getBlockSize();
            byte[] ivBytes = new byte[ivLen];
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            // 3 对输入流解密
            InputStream input = this.streamingData.getDataHandler().getInputStream();
            cache = new CachedOutputStream();
            cache.holdTempFile();
            CipherOutputStream output = new CipherOutputStream(cache, decryptCipher);
            IOUtils.copyLarge(input, output);
            output.flush();
            output.close();
            input.close();
            input = null;
            cache.lockOutputStream();

            // 4 用对方公钥对文件进行验证
            // 服务器证书私钥签名算法 SHA1withRSA
            String signatureAlgorithm = this.streamingData.getSignatureAlgorithm();
            Signature signature = Signature.getInstance(signatureAlgorithm);
            Crypto encryptionCrypto = cryptoMap.get(this.encPropFile);
            CryptoType aliasType = new CryptoType(CryptoType.TYPE.ALIAS);
            aliasType.setAlias(encryptionUser);
            signature.initVerify(encryptionCrypto.getX509Certificates(aliasType)[0]);
            signature.update(digestData);
            byte[] signedData = Base64.decode(this.streamingData.getSignatureValue());
            this.verifyValue = signature.verify(signedData);
        } catch (Exception e) {
            this.release();

            logger.info(e.getMessage());
            throw e;
        }
    }

    /**
     *
     */
    private void release() {
        if (cache != null) {
            cache.releaseTempFileHold();
        }
    }

    /**
     * @return the verifyValue
     * @throws IOException
     */
    public InputStream getDecryptStream() throws IOException {
        return cache.getInputStream();
    }

    /**
     * @return the verifyValue
     */
    public boolean getVerifyValue() {
        return verifyValue;
    }

    public StreamingData getDecryptStreamingData() throws Exception {
        try {
            String contentType = streamingData.getDataHandler().getContentType();
            streamingData.setDataHandler(
                    new DataHandler(new StreamDataSource(contentType, cache.getInputStream())));
        } catch (Exception e) {
            this.release();

            logger.info(e.getMessage());
            throw e;
        }
        this.release();
        return streamingData;
    }
}
