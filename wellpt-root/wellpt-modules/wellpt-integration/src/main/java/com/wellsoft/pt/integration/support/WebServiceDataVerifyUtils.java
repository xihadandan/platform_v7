/*
 * @(#)2014-1-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.cxf.aegis.type.mtom.StreamDataSource;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.apache.xpath.XPathAPI;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
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
 * 2014-1-24.1	zhulh		2014-1-24		Create
 * </pre>
 * @date 2014-1-24
 */
public class WebServiceDataVerifyUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String nodeName = "004140203/replyRequest";
        String filename = "2c97f19429623725012982f143856c4d_1.xml";
        try {
            verifySignature(nodeName, filename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }

    public static boolean verifySignature(String nodeName, String filename) throws Exception {
        //		FileService fileService = ApplicationContextHolder.getBean(FileService.class);
        //		String moduleName = "EXCHANGE_UPLOAD_FILE";
        //		String nodeName = "004140203SZ_BATCHID_002_sss/DATAID_002_sssf_1";
        //		String xmlFilename = "DATAID_002_sssf_1.xml";
        //		InputStream input = fileService.downFile(moduleName, nodeName, xmlFilename);
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                MongoFileService.class);
        String fileId = Md5PasswordEncoderUtils.encodePassword(nodeName + filename, null);
        MongoFileEntity mfe = mongoFileService.getFile(fileId);
        InputStream input = mfe.getInputstream();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document document = builder.parse(input);
        // String xml = IOUtils.toString(input);
        Element nscontext = createDSctx(document, "ds", Constants.SignatureSpecNS);
        Element sigElement = (Element) XPathAPI.selectSingleNode(document, "//ds:Signature[1]",
                nscontext);
        XMLSignature signature = new XMLSignature(sigElement, null);
        signature.addResourceResolver(new WebServiceDataOfflineResolver());
        String alias = "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f";
        X509Certificate cert = (X509Certificate) MerlinCrypto.getInstace().getCertificateByAlias(
                alias);
        boolean result = signature.checkSignatureValue(cert);
        return result;
    }

    public static boolean verifyAttachment(String nodeName) throws Exception {
        //		FileService fileService = ApplicationContextHolder.getBean(FileService.class);
        //		String moduleName = "EXCHANGE_UPLOAD_FILE";
        //		String nodeName = "004140203SZ_BATCHID_002_sss/DATAID_002_sssf_1/streamingData";

        MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                MongoFileService.class);
        String folderId = Md5PasswordEncoderUtils.encodePassword(nodeName, null);

        List<MongoFileEntity> mfes = mongoFileService.getFilesFromFolder(folderId, "exchangedata");

        //		List<FileEntity> fileEntities = fileService.downFiles(moduleName, nodeName);
        Map<String, Map<String, MongoFileEntity>> mongoFileEntityMap = new HashMap<String, Map<String, MongoFileEntity>>();
        for (MongoFileEntity mfe : mfes) {
            String filename = mfe.getFileName();
            String key = filename;
            int position = filename.lastIndexOf(".xml");
            if (position != -1) {
                key = filename.substring(0, position);
            }
            if (!mongoFileEntityMap.containsKey(key)) {
                mongoFileEntityMap.put(key, new HashMap<String, MongoFileEntity>());
            }
            Map<String, MongoFileEntity> map = mongoFileEntityMap.get(key);
            map.put(filename, mfe);
        }

        boolean signatureResult = false;
        for (String key : mongoFileEntityMap.keySet()) {
            Map<String, MongoFileEntity> map = mongoFileEntityMap.get(key);
            MongoFileEntity signatureEntity = map.get(key + ".xml");
            Map<String, String> jsonMap = objectMapper.readValue(signatureEntity.getInputstream(),
                    new HashMap<String, String>().getClass());

            StreamingData streamingData = fillStreamingData(jsonMap);
            MongoFileEntity mongoFileEntity = map.get(key);
            streamingData.setDataHandler(
                    new DataHandler(new StreamDataSource("octet-stream", mongoFileEntity
                            .getInputstream())));

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    Base64.decode(jsonMap.get("certificate").toString()));
            X509Certificate cert = (X509Certificate) cf.generateCertificate(bais);
            Map<String, Object> props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForX509Cert(
                    cert);
            WSS4JIntAttachment attachment = new WSS4JIntAttachment(streamingData, props);
            attachment.verifyAndDecrypt();
            signatureResult = attachment.getVerifyValue();
            if (!signatureResult) {
                break;
            }
        }
        return signatureResult;
    }

    /**
     * 如何描述该方法
     *
     * @param jsonMap
     * @return
     */
    private static StreamingData fillStreamingData(Map<String, String> jsonMap) {
        StreamingData streamingData = new StreamingData();
        String cipherAlgorithm = jsonMap.get("cipherAlgorithm") == null ? "" : jsonMap.get(
                "cipherAlgorithm")
                .toString();
        String digestAlgorithm = jsonMap.get("digestAlgorithm") == null ? "" : jsonMap.get(
                "digestAlgorithm")
                .toString();
        String digestValue = jsonMap.get("digestValue") == null ? "" : jsonMap.get(
                "digestValue").toString();
        String encryptedKey = jsonMap.get("encryptedKey") == null ? "" : jsonMap.get(
                "encryptedKey").toString();
        String encryptedKeyAlgorithm = jsonMap.get(
                "encryptedKeyAlgorithm") == null ? "" : jsonMap.get(
                "encryptedKeyAlgorithm").toString();
        String encryptedKeyCipherAlgorithm = jsonMap.get(
                "encryptedKeyCipherAlgorithm") == null ? "" : jsonMap.get(
                "encryptedKeyCipherAlgorithm").toString();
        String fileName = jsonMap.get("fileName") == null ? "" : jsonMap.get("fileName").toString();
        String signatureAlgorithm = jsonMap.get("signatureAlgorithm") == null ? "" : jsonMap.get(
                "signatureAlgorithm")
                .toString();
        String signatureValue = jsonMap.get("signatureValue") == null ? "" : jsonMap.get(
                "signatureValue").toString();
        streamingData.setCipherAlgorithm(cipherAlgorithm);
        streamingData.setDigestAlgorithm(digestAlgorithm);
        streamingData.setDigestValue(digestValue);
        streamingData.setEncryptedKey(encryptedKey);
        streamingData.setEncryptedKeyAlgorithm(encryptedKeyAlgorithm);
        streamingData.setEncryptedKeyCipherAlgorithm(encryptedKeyCipherAlgorithm);
        streamingData.setFileName(fileName);
        streamingData.setSignatureAlgorithm(signatureAlgorithm);
        streamingData.setSignatureValue(signatureValue);
        return streamingData;
    }

    public static Element createDSctx(Document doc, String prefix, String namespace) {
        if ((prefix == null) || (prefix.trim().length() == 0)) {
            throw new IllegalArgumentException("You must supply a prefix");
        }

        Element ctx = doc.createElementNS(null, "namespaceContext");

        ctx.setAttributeNS(Constants.NamespaceSpecNS, "xmlns:" + prefix.trim(), namespace);

        return ctx;
    }
}
