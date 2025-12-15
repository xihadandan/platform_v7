/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.facade.DataExchangeWebService;
import com.wellsoft.pt.integration.interceptor.CustomWSS4JInInterceptor;
import com.wellsoft.pt.integration.provider.BusinessHandleSource;
import com.wellsoft.pt.integration.request.*;
import com.wellsoft.pt.integration.response.*;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.integration.service.SerializeExchangeService;
import com.wellsoft.pt.integration.support.*;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.cxf.aegis.type.mtom.StreamDataSource;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.dom.WSSecurityEngineResult;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.security.auth.x500.X500Principal;
import javax.xml.ws.WebServiceContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;
//import com.wellsoft.app.xzsp.service.ProjectService;
//import com.wellsoft.app.xzsp.service.ZYDJService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-8.1	zhulh		2013-11-8		Create
 * </pre>
 * @date 2013-11-8
 */
@WebService
@SOAPBinding(style = Style.RPC)
public class DataExchangeWebServiceImpl implements DataExchangeWebService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private WebServiceContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    /*************************商改**************************************/
    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#send(com.wellsoft.pt.integration.request.SendRequest)
     */
    @Override
    public SendResponse send(SendRequest request) {
        logger.error("请求send:" + request.getFrom());
        SendResponse response = null;
        try {
            checkAndPrepare(request.getFrom(), "");

            // 附件数据解密等处理
            decryptedAndPrepareStreamingData(request);

            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.send(request, 1);

        } catch (Exception e) {
            response = new SendResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));

            logger.info(e.getMessage());
            logger.error(response.getMsg());
        }
        return response;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#sendCallback(com.wellsoft.pt.integration.request.ClientSendCallbackMessage)
     */
    @Override
    public PlatformSendCallbackResponse platformSendCallback(PlatformSendCallbackRequest request) {
        PlatformSendCallbackResponse response = null;
        logger.error("请求platformSendCallback:" + request.getUnitId());
        try {
            checkAndPrepare(request.getUnitId(), "");

            // 上传SOAP的XML内容到Mongo，节点内容(单位id/platformSendCallbackRequest/数据id +
            // "_" + 版本号 + ".xml")
            // String xmlFolder = request.getUnitId() +
            // "/platformSendCallbackRequest";
            // String xmlFilename = request.getDataId() + "_" +
            // request.getRecVer() + ".xml";
            // uploadXmlToMongo(xmlFolder, xmlFilename, request.getUnitId());

            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.sendCallback(request);
        } catch (Exception e) {
            response = new PlatformSendCallbackResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));

            logger.info(e.getMessage());
            logger.error(response.getMsg());
        }
        return response;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#reply(com.wellsoft.pt.integration.request.ReplyRequest)
     */
    @Override
    public ReplyResponse replyMsg(ReplyRequest request) {
        logger.error("请求replyMsg:" + request.getUnitId());
        ReplyResponse response = null;
        try {
            checkAndPrepare(request.getUnitId(), "");

            // 上传SOAP的XML内容到Mongo，节点内容(单位id/replyRequest/数据id + "_" + 版本号 +
            // ".xml")
            // String xmlFolder = request.getUnitId() + "/replyRequest";
            // String xmlFilename = request.getDataId() + "_" +
            // request.getRecVer() + ".xml";
            // uploadXmlToMongo(xmlFolder, xmlFilename, request.getUnitId());

            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.reply(request);
        } catch (Exception e) {
            response = new ReplyResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));

            logger.info(e.getMessage());
            logger.error(response.getMsg());
        }
        return response;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#cancel(com.wellsoft.pt.integration.request.CancelRequest)
     */
    @Override
    public CancelResponse cancel(CancelRequest request) {
        logger.error("请求cancel:" + request.getFromId());
        CancelResponse response = null;
        try {
            checkAndPrepare(request.getFromId(), "");

            // 上传SOAP的XML内容到Mongo，节点内容(单位id/cancelRequest/数据id + "_" + 版本号 +
            // ".xml")
            // String xmlFolder = request.getFromId() + "/cancelRequest";
            // String xmlFilename = request.getDataId() + "_" +
            // request.getRecVer() + ".xml";
            // uploadXmlToMongo(xmlFolder, xmlFilename, request.getFromId());

            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.cancel(request);
        } catch (Exception e) {
            response = new CancelResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));

            logger.info(e.getMessage());
            logger.error(response.getMsg());
        }
        return response;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#platformCancelCallback(com.wellsoft.pt.integration.request.PlatformCancelCallbackRequest)
     */
    @Override
    public PlatformCancelCallbackResponse platformCancelCallback(
            PlatformCancelCallbackRequest request) {
        logger.error("请求platformCancelCallback:" + request.getFromId());
        PlatformCancelCallbackResponse response = null;
        try {
            checkAndPrepare(request.getFromId(), "");

            // 上传SOAP的XML内容到Mongo，节点内容(单位id/platformCancelCallbackRequest/数据id +
            // "_" + 版本号 + ".xml")
            // String xmlFolder = request.getFromId() +
            // "/platformCancelCallbackRequest";
            // String xmlFilename = request.getDataId() + "_" +
            // request.getRecVer() + ".xml";
            // uploadXmlToMongo(xmlFolder, xmlFilename, request.getFromId());

            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.platformCancelCallback(request);
        } catch (Exception e) {
            response = new PlatformCancelCallbackResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));

            logger.info(e.getMessage());
            logger.error(response.getMsg());
        }
        return response;
    }

    @Override
    public Boolean historyDataRequest(String zch, String unitId) {
        logger.error("请求historyDataRequest:" + unitId);
        try {
            checkAndPrepare(unitId, "");
            // 上传SOAP的XML内容到Mongo
            // String xmlFolderName = zch + "_" + unitId;
            // String xmlFilename = Calendar.getInstance().getTimeInMillis() +
            // ".xml";
            // uploadXmlToMongo(xmlFolderName, xmlFilename, unitId);
            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            Boolean flag = exchangeDataFlowService.historyDataRequest(zch, unitId);
            return flag;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 附件数据解密等处理，商改数据交换接口
     *
     * @param request
     * @throws Exception
     */
    private void decryptedAndPrepareStreamingData(SendRequest request) throws Exception {
        try {
            WSSecurityEngineResult wser = (WSSecurityEngineResult) context.getMessageContext().get(
                    WSS4JInInterceptor.SIGNATURE_RESULT);
            X509Certificate cert = (X509Certificate) wser.get(
                    WSSecurityEngineResult.TAG_X509_CERTIFICATE);

            // SOAP内容xml及附件上传到Mongo
            // uploadToMongo(request, cert);

            // 附件验签解密
            Map<String, Object> props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForX509Cert(
                    cert);
            for (DataItem dataItem : request.getDataList()) {
                List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
                List<StreamingData> decryptedStreamingDatas = new ArrayList<StreamingData>();
                for (StreamingData streamingData : streamingDatas) {
                    WSS4JIntAttachment wss4jIntAttachment = null;
                    try {
                        wss4jIntAttachment = new WSS4JIntAttachment(streamingData, props);
                        wss4jIntAttachment.verifyAndDecrypt();
                        if (!wss4jIntAttachment.getVerifyValue()) {
                            throw new RuntimeException(
                                    "附件[" + streamingData.getFileName() + "]签名验证失败!");
                        }
                        decryptedStreamingDatas.add(wss4jIntAttachment.getDecryptStreamingData());
                    } catch (Exception e) {
                        logger.info(e.getMessage());

                        throw e;
                    }

                }
                dataItem.setStreamingDatas(decryptedStreamingDatas);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());

            throw e;
        }
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @param cert
     * @throws Exception
     * @throws IOException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws CertificateEncodingException
     */
    private void uploadToMongo(SendRequest request,
                               X509Certificate cert) throws Exception, IOException,
            JsonGenerationException, JsonMappingException, CertificateEncodingException {
        try {
            // XML及附件信息存储到mongo中
            // 上传SOAP的XML内容到Mongo，内容结点(单位id/sendRequest/类型id + "_" + 批次id/类型id +
            // "_" + 批次号 + "_" + 当前时间缀 + ".xml")
            // 附件保存，内容结点(单位id/sendRequest/数据ID + "_" + 数据版本号 +
            // "/streamingData/附件名称")
            // 附件签名，内容节点(单位id/sendRequest/数据ID + "_" + 数据版本号 +
            // "/streamingData/附件名称 + "_sign.xml")

            // 上传SOAP的XML内容到Mongo
            String xmlFolder = request.getFrom() + "/sendRequest/" + request.getTypeId() + "_" + request.getBatchId();
            String xmlFilename = request.getTypeId() + "_" + request.getBatchId() + "_"
                    + Calendar.getInstance().getTimeInMillis() + ".xml";
            uploadXmlToMongo(xmlFolder, xmlFilename, request.getFrom());

            String tenantId = getTanentId(request.getFrom());
            IgnoreLoginUtils.login(tenantId, tenantId);

            // 附件处理及附件签名
            MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                    MongoFileService.class);
            for (DataItem dataItem : request.getDataList()) {
                String folder = request.getFrom() + "/sendRequest/" + dataItem.getDataId() + "_" + dataItem.getRecVer()
                        + "/streamingData/";
                List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
                for (StreamingData streamingData : streamingDatas) {
                    // 上传附件到Mongo
                    String filename = streamingData.getFileName();
                    InputStream tmpIns = streamingData.getDataHandler().getDataSource().getInputStream();
                    InputStream in = streamingData.getDataHandler().getDataSource().getInputStream();

                    String folderId = Md5PasswordEncoderUtils.encodePassword(folder, null);
                    String fileId = Md5PasswordEncoderUtils.encodePassword(folder + filename,
                            null) + "_"
                            + Math.random() * 10000;

                    mongoFileService.saveFile(fileId, filename, in);
                    mongoFileService.pushFileToFolder(folderId, fileId, "");

                    streamingData.setDataHandler(
                            new DataHandler(new StreamDataSource(streamingData.getDataHandler()
                                    .getDataSource().getContentType(), tmpIns)));
                    logger.info(
                            "upload soap file to mongo, [folderId:" + folderId + ",fileId:" + fileId + ",filename:"
                                    + filename + "]");

                    // 上传附件签名信息到Mongo
                    String signatureFilename = filename + "_sign.xml";
                    ByteArrayOutputStream signatureBaos = new ByteArrayOutputStream();
                    String fileId2 = Md5PasswordEncoderUtils.encodePassword(
                            folder + signatureFilename, null) + "_"
                            + Math.random() * 10000;
                    mongoFileService.saveFile(fileId2, signatureFilename,
                            new ByteArrayInputStream(signatureBaos.toByteArray()));
                    mongoFileService.pushFileToFolder(folderId, fileId2, "");

                    logger.info(
                            "upload soap file to mongo, [[folderId:" + folderId + ",fileId:" + fileId2
                                    + ",filename:" + signatureFilename + "]");
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * 上传SOAP的XML内容到Mongo
     *
     * @param request
     * @param fileService
     * @param envelopeXmlBaos
     * @return
     */
    private void uploadXmlToMongo(String folerName, String filename, String unitId) {
        String tenantId = getTanentId(unitId);
        try {
            IgnoreLoginUtils.logout();
            IgnoreLoginUtils.login(tenantId, tenantId);

            MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                    MongoFileService.class);
            ByteArrayOutputStream envelopeXmlBaos = (ByteArrayOutputStream) context.getMessageContext().get(
                    CustomWSS4JInInterceptor.CUSTOM_DECRYPT_RESULT);

            ByteArrayInputStream bais = new ByteArrayInputStream(envelopeXmlBaos.toByteArray());
            String folderId = Md5PasswordEncoderUtils.encodePassword(folerName, null);
            String fileId = Md5PasswordEncoderUtils.encodePassword(folerName + filename,
                    null) + "_" + Math.random()
                    * 10000;
            mongoFileService.saveFile(fileId, filename, bais);
            mongoFileService.pushFileToFolder(folderId, fileId, "");

            logger.info(
                    "upload soap xml to mongo, [" + folderId + "," + fileId + ", " + filename + "]");
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * 从StreamingData中取出签名等信息
     *
     * @param streamingData
     * @param cert
     * @return
     * @throws CertificateEncodingException
     */
    private Map<String, String> retrieveJsonData(StreamingData streamingData,
                                                 X509Certificate certificate)
            throws CertificateEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cipherAlgorithm", streamingData.getCipherAlgorithm());
        map.put("digestAlgorithm", streamingData.getDigestAlgorithm());
        map.put("digestValue", streamingData.getDigestValue());
        map.put("encryptedKey", streamingData.getEncryptedKey());
        map.put("encryptedKeyAlgorithm", streamingData.getEncryptedKeyAlgorithm());
        map.put("encryptedKeyCipherAlgorithm", streamingData.getEncryptedKeyCipherAlgorithm());
        map.put("fileName", streamingData.getFileName());
        map.put("signatureAlgorithm", streamingData.getSignatureAlgorithm());
        map.put("signatureValue", streamingData.getSignatureValue());
        map.put("certificate",
                org.apache.xml.security.utils.Base64.encode(certificate.getEncoded()));
        return map;
    }

    /**
     * 检验调用源系统单位
     *
     * @param fromUnitId
     */
    private void checkAndPrepare(String fromUnitId, String blr) {
        if (StringUtils.isBlank(fromUnitId)) {
            throw new RuntimeException("源单位ID[" + fromUnitId + "]不能为空!");
        }
        // System.out.println(results);
        X500Principal principal = (X500Principal) context.getUserPrincipal();
        // System.out.println("X500Principal: " + principal.getName());
        // System.out.println("X500Principal: " +
        // principal.getName(X500Principal.RFC1779));
        // System.out.println("X500Principal: " +
        // principal.getName(X500Principal.RFC2253));
        // System.out.println("X500Principal: " +
        // principal.getName(X500Principal.CANONICAL));
        // logger.error("X500Principal: " + principal.getName());
        // logger.error("X500Principal: " +
        // principal.getName(X500Principal.RFC1779));
        // logger.error("X500Principal: " +
        // principal.getName(X500Principal.RFC2253));
        // logger.error("X500Principal: " +
        // principal.getName(X500Principal.CANONICAL));

        String tenantId = getTanentId(fromUnitId);
        String loginName = principal.toString();

        // 检验是否设置证书
        ExchangeDataConfigService exchangeDataConfigService = ApplicationContextHolder
                .getBean(ExchangeDataConfigService.class);
        ExchangeSystem example = new ExchangeSystem();
        example.setUnitId(fromUnitId);
        example.setSubjectDN(loginName);
        List<ExchangeSystem> exchangeSystems = exchangeDataConfigService.findByExample(example);
        if (exchangeSystems.isEmpty()) {
            throw new RuntimeException(
                    "找不到证书主体为[" + loginName + "],单位Id为[" + fromUnitId + "]的接入系统");
        }
        ExchangeSystem exchangeSystem = exchangeSystems.get(0);
        String systemName = exchangeSystem.getName();

        ExchangeDataTypeService exchangeDataTypeService = ApplicationContextHolder
                .getBean(ExchangeDataTypeService.class);
        List<ExchangeDataType> exchangeDataTypes = exchangeDataTypeService.getExchangeDataTypesByTypeIds(
                exchangeSystem
                        .getTypeId());
        if (exchangeDataTypes.isEmpty()) {
            throw new RuntimeException("接入系统[" + systemName + "]的数据类型没有设置业务类型");
        }
        ExchangeDataType exchangeDataType = exchangeDataTypes.get(0);

        UnitApiFacade unitApiFacade = ApplicationContextHolder.getBean(UnitApiFacade.class);
        // 获取单位业务负责人
        List<OrgUserVo> users = unitApiFacade.getBusinessUnitManagerById(
                exchangeDataType.getBusinessTypeId(),
                fromUnitId);
        if (users.isEmpty()) {
            throw new RuntimeException("接入系统[" + systemName + "]所在单位没有设置业务管理员");
        }

        OrgUserVo user = new OrgUserVo();
        if (StringUtils.isBlank(blr)) {
            user = users.get(0);
        } else {
            for (OrgUserVo u : users) {
                String usn = u.getUserName();
                if (usn.equals(blr)) {
                    user = u;
                    break;
                }
            }
            if (StringUtils.isBlank(user.getUuid())) {
                user = users.get(0);
            }
        }
        // 模拟单位用户登录
        if (!isCommunal()) {
            TenantFacadeService tenantService = ApplicationContextHolder.getBean(
                    TenantFacadeService.class);
            Tenant tenant = null;
            tenant = tenantService.getByAccount(tenantId);
            UserDetails userDetails = new DefaultUserDetails(tenant, user,
                    AuthorityUtils.createAuthorityList());
            CommonUnit commonUnit = unitApiFacade.getCommonUnitById(fromUnitId);
            CommonUnit unit = new CommonUnit();
            BeanUtils.copyProperties(commonUnit, unit);
            // userDetails.setCommonUnit(exchangeDataType.getBusinessTypeId(),
            // commonUnit);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }
    }

    /*************************数据交换**************************************/

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#send(com.wellsoft.pt.integration.request.DXRequest)
     */
    @Override
    public DXResponse dxSend(DXRequest request) {
        DXResponse response = null;
        // 验证数据是否满足业务需求
        ExchangeDataConfigService exchangeDataConfigService = ApplicationContextHolder
                .getBean(ExchangeDataConfigService.class);
        ExchangeDataType exchangeDataType = exchangeDataConfigService.getExType(
                request.getTypeId());// 数据类型
        Map<String, BusinessHandleSource> businessHandleSourceMap = ApplicationContextHolder.getApplicationContext()
                .getBeansOfType(BusinessHandleSource.class);
        for (DXDataItem d : request.getDataList()) {
            Map<Integer, String> backMap = businessHandleSourceMap.get(
                    exchangeDataType.getBusinessId().split(":")[0])
                    .correctnessData(d.getText(), request);
            for (Map.Entry<Integer, String> entry : backMap.entrySet()) {
                Integer key = entry.getKey();
                if (key == -100 || key == -101) {
                    response = new DXResponse();
                    response.setCode(-1);
                    Map<String, String> values = new HashMap<String, String>();
                    if (key == -100) {
                        values.put("XMBH", entry.getValue());
                        values.put("XMDBH", backMap.get(-101));
                    } else if (key == -101) {
                        values.put("XMBH", backMap.get(-100));
                        values.put("XMDBH", entry.getValue());
                    }
                    values.put("ERROR", "-100");
                    response.setParams(values);
                    return response;
                } else if (key != 0) {// 验证不成功时直接返回验证失败的结果
                    response = new DXResponse();
                    response.setCode(-1);
                    response.setMsg(entry.getValue());
                    return response;
                }
            }
        }

        try {
            String blrmc = "";
            try {
                blrmc = DocumentHelper.parseText(
                        request.getDataList().get(0).getText()).getRootElement()
                        .elementText("BLRMC");
            } catch (Exception e) {
            }
            checkAndPrepare(request.getFrom(), blrmc);
            // 附件数据解密等处理
            dxDecryptedAndPrepareStreamingData(request);
            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.dxSend(request, ExchangeConfig.SOURCE_EXTERNAL);
        } catch (BusinessException b) {
            response = new DXResponse();
            response.setCode(-1);
            response.setMsg(b.getMessage());
            logger.error(response.getMsg());
        } catch (Exception e) {
            response = new DXResponse();
            response.setCode(-1);
            response.setMsg(ExceptionUtils.getStackTrace(e));
            logger.error(response.getMsg());
        }
        return response;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#sendCallback(com.wellsoft.pt.integration.request.ClientSendCallbackMessage)
     */
    @Override
    public DXResponse dxCallback(DXCallbackRequest request) {
        DXResponse response = null;
        try {
            checkAndPrepare(request.getUnitId(), "");
            // 上传SOAP的XML内容到Mongo
            // String xmlFolderName = request.getBatchId();
            // String xmlFilename = request.getBatchId() + ".xml";
            // uploadXmlToMongo(xmlFolderName, xmlFilename,
            // request.getUnitId());
            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            response = exchangeDataFlowService.dxCallback(request);
        } catch (Exception e) {
            response = new DXResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));
            logger.error(response.getMsg());
        }
        return response;
    }

    @Override
    public ShareResponse query(ShareRequest shareRequest) {
        ShareResponse shareResponse = new ShareResponse();
        String unitId = shareRequest.getUnitId();
        try {
            checkAndPrepare(unitId, "");
            // 上传SOAP的XML内容到Mongo
            // String xmlFolderName = typeId + "_" + unitId;
            // String xmlFilename = Calendar.getInstance().getTimeInMillis() +
            // ".xml";
            // uploadXmlToMongo(xmlFolderName, xmlFilename, unitId);
            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            shareResponse = exchangeDataFlowService.query(shareRequest, "");
        } catch (Exception e) {
            shareResponse.setCode(-2);
            shareResponse.setMsg(ExceptionUtils.getStackTrace(e));
            logger.error(shareResponse.getMsg());
        }
        return shareResponse;
    }

    @Override
    public DXResponse dxCancel(DXCancelRequest dXCancelRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 附件数据解密等处理
     *
     * @param request
     * @throws Exception
     */
    private void dxDecryptedAndPrepareStreamingData(DXRequest request) throws Exception {
        try {
            WSSecurityEngineResult wser = (WSSecurityEngineResult) context.getMessageContext().get(
                    WSS4JInInterceptor.SIGNATURE_RESULT);
            X509Certificate cert = (X509Certificate) wser.get(
                    WSSecurityEngineResult.TAG_X509_CERTIFICATE);
            // SOAP内容xml及附件上传到Mongo
            // dxUploadToMongo(request, cert);
            // 附件验签解密
            Map<String, Object> props = MerlinCrypto.getInstace().getWSS4JAttachmentPropsForX509Cert(
                    cert);
            for (DXDataItem dataItem : request.getDataList()) {
                List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
                List<StreamingData> decryptedStreamingDatas = new ArrayList<StreamingData>();
                for (StreamingData streamingData : streamingDatas) {
                    WSS4JIntAttachment wss4jIntAttachment = null;
                    try {
                        wss4jIntAttachment = new WSS4JIntAttachment(streamingData, props);
                        wss4jIntAttachment.verifyAndDecrypt();
                        if (!wss4jIntAttachment.getVerifyValue()) {
                            throw new RuntimeException(
                                    "附件[" + streamingData.getFileName() + "]签名验证失败!");
                        }
                        decryptedStreamingDatas.add(wss4jIntAttachment.getDecryptStreamingData());
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                        throw e;
                    }
                }
                dataItem.setStreamingDatas(decryptedStreamingDatas);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw e;
        }
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @param cert
     * @throws Exception
     * @throws IOException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws CertificateEncodingException
     */
    private void dxUploadToMongo(DXRequest request,
                                 X509Certificate cert) throws Exception, IOException,
            JsonGenerationException, JsonMappingException, CertificateEncodingException {
        // XML及附件信息存储到mongo中
        // 上传SOAP的XML内容到Mongo，内容结点(批次id + "_" + 类型id +"/" + 批次id + "_" + 类型id +
        // ".xml")
        // 附件保存，内容结点(批次id + "_" + 类型id + "_" + 数据id + "_" + 版本号 +
        // "/streamingData/" + 附件名称)
        // 附件签名，内容节点(批次id + "_" + 类型id + "_" + 数据id + "_" + 版本号 +
        // "/streamingData/" + 附件名称 + "_sign.xml")
        try {
            String xmlFolder = request.getBatchId() + "_" + request.getTypeId();
            String xmlFilename = request.getBatchId() + "_" + request.getTypeId() + ".xml";
            uploadXmlToMongo(xmlFolder, xmlFilename, request.getFrom());
            String tenantId = getTanentId(request.getFrom());
            IgnoreLoginUtils.login(tenantId, tenantId);
            MongoFileService mongoFileService = ApplicationContextHolder.getBean(
                    MongoFileService.class);
            for (DXDataItem dataItem : request.getDataList()) {
                List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
                String folderName = request.getBatchId() + "_" + request.getTypeId() + "_" + dataItem.getDataId() + "_"
                        + dataItem.getRecVer() + "/streamingData/";
                for (StreamingData streamingData : streamingDatas) {
                    // 上传附件到mongo
                    String filename = streamingData.getFileName();
                    InputStream tmpIns = streamingData.getDataHandler().getDataSource().getInputStream();
                    InputStream in = streamingData.getDataHandler().getDataSource().getInputStream();
                    String folderId = Md5PasswordEncoderUtils.encodePassword(folderName, null);
                    String fileId = Md5PasswordEncoderUtils.encodePassword(folderName + filename,
                            null);
                    mongoFileService.saveFile(fileId, filename, in);
                    mongoFileService.pushFileToFolder(folderId, fileId, "");

                    streamingData.setDataHandler(
                            new DataHandler(new StreamDataSource(streamingData.getDataHandler()
                                    .getDataSource().getContentType(), tmpIns)));
                    logger.info(
                            "upload soap file to Mongo, [folderId:" + folderId + ",fileId:" + fileId + ",filename:"
                                    + filename + "]");
                    // 上传附件签名信息到Mongo
                    String signatureFilename = filename + "_sign.xml";
                    ByteArrayOutputStream signatureBaos = new ByteArrayOutputStream();
                    objectMapper.writeValue(signatureBaos, retrieveJsonData(streamingData, cert));
                    String fileId2 = Md5PasswordEncoderUtils.encodePassword(
                            folderName + signatureFilename, null);
                    mongoFileService.saveFile(fileId2, signatureFilename,
                            new ByteArrayInputStream(signatureBaos.toByteArray()));
                    mongoFileService.pushFileToFolder(folderId, fileId2, "");

                    logger.info(
                            "upload soap file to Mongo, [folderId:" + folderId + ",fileId:" + fileId2
                                    + ",filename:" + signatureFilename + "]");
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    public String getTanentId(String unitId) {
        String tenantId = TenantContextHolder.getTenantId();
        String tenantId2 = Config.COMMON_TENANT;
        if (tenantId.equals(tenantId2) && !"".equals(unitId)) {// tenantId为公共库租户Id时，请求的为公共WebService
            UnitApiFacade unitApiFacade = ApplicationContextHolder.getBean(UnitApiFacade.class);
            tenantId = unitApiFacade.getTenantIdByCommonUnitId(unitId);
        }
        return tenantId;
    }

    public boolean isCommunal() {
        String tenantId = TenantContextHolder.getTenantId();
        String tenantId2 = Config.COMMON_TENANT;
        if (tenantId.equals(tenantId2)) // tenantId为公共库租户Id时，请求的为公共WebService
            return true;
        else
            return false;
    }

    @Override
    public Boolean BridgeSyn(String entityName, String unitId, String str) {
        // TODO Auto-generated method stub
        // ZYDJService zydjService =
        // ApplicationContextHolder.getBean(ZYDJService.class);
        // return zydjService.dridgeSyn(entityName, unitId, str);

        // 业务代码，屏蔽功能，返回false --zyguo
        return false;
    }

    @Override
    public String getTest(String getData) {
        // TODO Auto-generated method stub
        ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                .getBean(ExchangeDataFlowService.class);
        exchangeDataFlowService.getTest(getData);
        return getData;
    }

    /**
     * 标识项目已经获取过
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebService#xmflag(java.lang.String)
     */
    @Override
    public Boolean xmflag(String xmbh, String unitId) {
        logger.error("xmflag请求参数：xmbh → 【" + xmbh + "】,unitId → 【" + unitId + "】");
        Boolean xmflagResponse = false;
        try {
            // checkAndPrepare(unitId, "");
            // ProjectService projectService =
            // ApplicationContextHolder.getBean(ProjectService.class);
            // return projectService.markProjectPicked(xmbh, unitId);
            // 业务代码，屏蔽功能，返回false --zyguo
            return false;
        } catch (Exception e) {
            logger.error("xmflag请求返回异常:" + e.getMessage());
            logger.info(e.getMessage());
        }
        return xmflagResponse;
    }

    @Override
    public Response SendSerializeData(String typeId, String unitId, String serializeData,
                                      String para,
                                      FilesRequest streamingDatas) {
        // TODO Auto-generated method stub
        SerializeExchangeService serializeExchangeService = ApplicationContextHolder
                .getBean(SerializeExchangeService.class);
        try {
            return serializeExchangeService.onReceive(typeId, unitId, serializeData, para,
                    streamingDatas);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
