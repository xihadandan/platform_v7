package com.wellsoft.pt.di.request;

import javax.activation.DataHandler;
import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/13    chenq		2019/8/13		Create
 * </pre>
 */
public class RequestStream implements Serializable {

    protected String fileName;

    protected DataHandler dataHandler;

    protected String encryptedKey;

    protected String encryptedKeyAlgorithm = "AES";

    protected String encryptedKeyCipherAlgorithm = "AES/CBC/ISO10126Padding";

    protected String cipherAlgorithm = "RSA/ECB/PKCS1Padding";

    protected String digestValue;

    protected String digestAlgorithm = "SHA-1";

    protected String signatureValue;

    protected String signatureAlgorithm = "SHA1withRSA";


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getEncryptedKeyAlgorithm() {
        return encryptedKeyAlgorithm;
    }

    public void setEncryptedKeyAlgorithm(String encryptedKeyAlgorithm) {
        this.encryptedKeyAlgorithm = encryptedKeyAlgorithm;
    }

    public String getEncryptedKeyCipherAlgorithm() {
        return encryptedKeyCipherAlgorithm;
    }

    public void setEncryptedKeyCipherAlgorithm(String encryptedKeyCipherAlgorithm) {
        this.encryptedKeyCipherAlgorithm = encryptedKeyCipherAlgorithm;
    }

    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    public void setCipherAlgorithm(String cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    public String getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }
}
