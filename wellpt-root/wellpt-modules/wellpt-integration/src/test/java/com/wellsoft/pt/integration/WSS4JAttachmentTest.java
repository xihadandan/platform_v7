/*
 * @(#)2014-1-22 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.integration.support.MerlinCrypto;
import com.wellsoft.pt.integration.support.StreamingData;
import com.wellsoft.pt.integration.support.WSS4JIntAttachment;
import com.wellsoft.pt.integration.support.WSS4JOutAttachment;
import org.apache.commons.io.IOUtils;
import org.apache.xml.security.utils.Base64;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
public class WSS4JAttachmentTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File file = new File("F:\\nexus-2.1.2-bundle.zip");
        StreamingData sd = new StreamingData();
        sd.setDataHandler(new DataHandler(new FileDataSource(file)));
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user",
                "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser",
                "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("encryptionPropFile", "ca/clientStore-QYDJ.properties");
        outProps.put("signatureUser",
                "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        outProps.put("signaturePropFile", "ca/clientStore-QYDJ.properties");
        WSS4JOutAttachment wss4jOutAttachment = new WSS4JOutAttachment(sd, outProps);
        wss4jOutAttachment.signAndEncrypt();
        // System.out.println(wss4jOutAttachment.getDigestValue());
        // System.out.println(wss4jOutAttachment.getSignatureValue());
        // System.out.println(wss4jOutAttachment.getEncryptedKey());
        StreamingData tmpSd = wss4jOutAttachment.getEncryptedStreamingData();
        String encryptedKey = tmpSd.getEncryptedKey();
        String encryptedKeyAlgorithm = tmpSd.getEncryptedKeyAlgorithm();
        String encryptedKeyCipherAlgorithm = tmpSd.getEncryptedKeyCipherAlgorithm();
        String cipherAlgorithm = tmpSd.getCipherAlgorithm();
        String digestValue = tmpSd.getDigestValue();
        String digestAlgorithm = tmpSd.getDigestAlgorithm();
        String signatureValue = tmpSd.getSignatureValue();
        String signatureAlgorithm = tmpSd.getSignatureAlgorithm();
        String certificate = wss4jOutAttachment.getCertificate();

        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("encryptedKey", encryptedKey);
        jsonMap.put("encryptedKeyAlgorithm", encryptedKeyAlgorithm);
        jsonMap.put("encryptedKeyCipherAlgorithm", encryptedKeyCipherAlgorithm);
        jsonMap.put("cipherAlgorithm", cipherAlgorithm);
        jsonMap.put("digestValue", digestValue);
        jsonMap.put("digestAlgorithm", digestAlgorithm);
        jsonMap.put("signatureValue", signatureValue);
        jsonMap.put("signatureAlgorithm", signatureAlgorithm);
        jsonMap.put("certificate", certificate);

        System.out.println(JsonUtils.object2Json(jsonMap));

        File singEncFile = new File("F:\\CA_SIGN_ENC.txt");
        FileOutputStream singEncOout = new FileOutputStream(singEncFile);
        singEncOout.write(JsonUtils.object2Json(jsonMap).getBytes());
        singEncOout.flush();
        singEncOout.close();

        File encFile = new File("F:\\nexus-2.1.2-bundle.zip.data");
        FileOutputStream out1 = new FileOutputStream(encFile);
        InputStream in = tmpSd.getDataHandler().getInputStream();
        IOUtils.copyLarge(in, out1);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out1);

        // ///////////////////////////////////////////////////////////
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bais = new ByteArrayInputStream(
                Base64.decode(wss4jOutAttachment.getCertificate()));
        X509Certificate cert = (X509Certificate) cf.generateCertificate(bais);
        Map<String, Object> properties = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForX509Cert(
                cert);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("action", "Signature Encrypt");
        props.put("user", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        props.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        props.put("encryptionUser",
                "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        props.put("encryptionPropFile", "ca/serverStore-SSGL.properties");
        props.put("signatureUser",
                "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        props.put("signaturePropFile", "ca/serverStore-SSGL.properties");
        StreamingData streamingData = new StreamingData(
                new DataHandler(new FileDataSource(encFile)));
        streamingData.setEncryptedKey(encryptedKey);
        streamingData.setEncryptedKeyAlgorithm(encryptedKeyAlgorithm);
        streamingData.setEncryptedKeyCipherAlgorithm(encryptedKeyCipherAlgorithm);
        streamingData.setCipherAlgorithm(cipherAlgorithm);
        streamingData.setDigestValue(digestValue);
        streamingData.setDigestAlgorithm(digestAlgorithm);
        streamingData.setSignatureValue(signatureValue);
        streamingData.setSignatureAlgorithm(signatureAlgorithm);
        WSS4JIntAttachment wss4jIntAttachment = new WSS4JIntAttachment(streamingData, properties);
        wss4jIntAttachment.verifyAndDecrypt();
        // System.out.println(wss4jIntAttachment.getVerifyValue());
        // System.out.println(wss4jIntAttachment.getInputStream());
        InputStream ins = wss4jIntAttachment.getDecryptStream();
        FileOutputStream out3 = new FileOutputStream(new File("F:\\nexus-2.1.2-bundle2.zip"));
        IOUtils.copyLarge(ins, out3);
        IOUtils.closeQuietly(ins);
        IOUtils.closeQuietly(out3);
        ins = wss4jIntAttachment.getDecryptStream();
        FileOutputStream out4 = new FileOutputStream(new File("F:\\nexus-2.1.2-bundle3.zip"));
        IOUtils.copyLarge(ins, out4);
        IOUtils.closeQuietly(ins);
        IOUtils.closeQuietly(out4);
    }
}
