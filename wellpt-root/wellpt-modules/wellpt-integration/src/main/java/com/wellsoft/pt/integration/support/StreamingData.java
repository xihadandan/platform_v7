/*
 * @(#)2013-11-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import javax.activation.DataHandler;
import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-9.1	zhulh		2013-11-9		Create
 * </pre>
 * @date 2013-11-9
 */
public class StreamingData implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8804571124708988435L;

    private String fileName;

    private DataHandler dataHandler;

    private String encryptedKey;

    private String encryptedKeyAlgorithm = "AES";

    private String encryptedKeyCipherAlgorithm = "AES/CBC/ISO10126Padding";

    private String cipherAlgorithm = "RSA/ECB/PKCS1Padding";

    private String digestValue;

    private String digestAlgorithm = "SHA-1";

    private String signatureValue;

    private String signatureAlgorithm = "SHA1withRSA";

    /**
     *
     */
    public StreamingData() {
        super();
    }

    /**
     * @param dataHandler
     */
    public StreamingData(DataHandler dataHandler) {
        this(dataHandler.getName(), dataHandler);
    }

    /**
     * @param fileName
     * @param dataHandler
     */
    public StreamingData(String fileName, DataHandler dataHandler) {
        super();
        this.fileName = fileName;
        this.dataHandler = dataHandler;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName 要设置的fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the dataHandler
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * @param dataHandler 要设置的dataHandler
     */
    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    /**
     * @return the encryptedKey
     */
    public String getEncryptedKey() {
        return encryptedKey;
    }

    /**
     * @param encryptedKey 要设置的encryptedKey
     */
    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    /**
     * @return the encryptedKeyAlgorithm
     */
    public String getEncryptedKeyAlgorithm() {
        return encryptedKeyAlgorithm;
    }

    /**
     * @param encryptedKeyAlgorithm 要设置的encryptedKeyAlgorithm
     */
    public void setEncryptedKeyAlgorithm(String encryptedKeyAlgorithm) {
        this.encryptedKeyAlgorithm = encryptedKeyAlgorithm;
    }

    /**
     * @return the encryptedKeyCipherAlgorithm
     */
    public String getEncryptedKeyCipherAlgorithm() {
        return encryptedKeyCipherAlgorithm;
    }

    /**
     * @param encryptedKeyCipherAlgorithm 要设置的encryptedKeyCipherAlgorithm
     */
    public void setEncryptedKeyCipherAlgorithm(String encryptedKeyCipherAlgorithm) {
        this.encryptedKeyCipherAlgorithm = encryptedKeyCipherAlgorithm;
    }

    /**
     * @return the cipherAlgorithm
     */
    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    /**
     * @param cipherAlgorithm 要设置的cipherAlgorithm
     */
    public void setCipherAlgorithm(String cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    /**
     * @return the digestValue
     */
    public String getDigestValue() {
        return digestValue;
    }

    /**
     * @param digestValue 要设置的digestValue
     */
    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    /**
     * @return the digestAlgorithm
     */
    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * @param digestAlgorithm 要设置的digestAlgorithm
     */
    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    /**
     * @return the signatureValue
     */
    public String getSignatureValue() {
        return signatureValue;
    }

    /**
     * @param signatureValue 要设置的signatureValue
     */
    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    /**
     * @return the signatureAlgorithm
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * @param signatureAlgorithm 要设置的signatureAlgorithm
     */
    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

}
