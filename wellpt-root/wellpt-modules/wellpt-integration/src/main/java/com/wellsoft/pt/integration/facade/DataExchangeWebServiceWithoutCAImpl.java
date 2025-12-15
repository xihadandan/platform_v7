/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.provider.BusinessHandleSource;
import com.wellsoft.pt.integration.request.DXCallbackRequest;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.response.DXResponse;
import com.wellsoft.pt.integration.response.ShareResponse;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.integration.support.DXDataItem;
import com.wellsoft.pt.integration.support.InputStreamDataSource;
import com.wellsoft.pt.integration.support.StreamingData;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import sun.misc.BASE64Decoder;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
 * 2013-11-8.1	zhulh		2013-11-8		Create
 * </pre>
 * @date 2013-11-8
 */
@WebService
@SOAPBinding(style = Style.RPC)
public class DataExchangeWebServiceWithoutCAImpl implements DataExchangeWebServiceWithoutCA {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 源系统调用数据交换平台上传接口：
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebServiceWithoutCA#dxSend(java.lang.String)
     */
    @Override
    public String dxSend(String dXRequestXml) {
        String dxResponseXml = "";
        try {
            IgnoreLoginUtils.login("T001", "U0010000001");
            /****************将所传入的xml转为DxRequest对象开始********************/
            // logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxSend 请求参数:--------【"
            // + dXRequestXml + "】");
            // 实例化DXRequest
            System.out.println("#####################");
            DXRequest request = new DXRequest();
            try {
                // 创建集合存储 DXDataItem
                List<DXDataItem> dataItems = new ArrayList<DXDataItem>();
                Document document = DocumentHelper.parseText(dXRequestXml);
                // 获得根元素
                Element rootElement = document.getRootElement();
                // 获得根元素中包含的所有子元素
                List<Element> rootChildElements = rootElement.elements();
                for (int i = 0; i < rootChildElements.size(); i++) {
                    Element childElement = rootChildElements.get(i);
                    if ("FROM".equals(childElement.getName())) {
                        request.setFrom(childElement.getText());
                    } else if ("TO".equals(childElement.getName())) {
                        request.setTo(childElement.getText());
                    } else if ("CC".equals(childElement.getName())) {
                        request.setCc(childElement.getText());
                    } else if ("BCC".equals(childElement.getName())) {
                        request.setBcc(childElement.getText());
                    } else if ("TYPEID".equals(childElement.getName())) {
                        request.setTypeId(childElement.getText());
                    } else if ("BATCHID".equals(childElement.getName())) {
                        request.setBatchId(childElement.getText());
                    } else if ("DATALIST".equals(childElement.getName())) {
                        // 获得DATALIST Item子元素
                        List<Element> dataListElements = childElement.elements();
                        // 遍历DATALIST Items
                        for (int j = 0; j < dataListElements.size(); j++) {
                            // 创建实例化DXDataItem对象
                            DXDataItem dataItem = new DXDataItem();
                            // 获得DATALIST所有Item下的子元素
                            List<Element> dataListChildElements = ((Element) dataListElements.get(j)).elements();
                            // 遍历DATALIST所有Item下的子元素
                            for (int k = 0; k < dataListChildElements.size(); k++) {
                                // 获得DATALIST item
                                Element e1 = dataListChildElements.get(k);
                                // 获得 并填充DATAID RECVER TEXT
                                if ("DATAID".equals(e1.getName())) {
                                    dataItem.setDataId(e1.getText());
                                } else if ("RECVER".equals(e1.getName())) {
                                    dataItem.setRecVer(Integer.parseInt(e1.getText()));
                                } else if ("TEXT".equals(e1.getName())) {
                                    dataItem.setText(((Element) e1.elements("item").get(0)).asXML());
                                } else if ("STREAMINGDATAS".equals(e1.getName())) {
                                    // 创建STREAMINGDATAS集合
                                    List<StreamingData> streamingDatas = new ArrayList<StreamingData>();

                                    // 获得STREAMINGDATAS下的所有 item
                                    List<Element> smdChildElements = e1.elements();
                                    for (int n = 0; n < smdChildElements.size(); n++) {

                                        String fileName = "";
                                        String dataHanlerd = "";
                                        // 遍历STREAMINGDATAS item子元素的
                                        List<Element> e2 = smdChildElements.get(n).elements();
                                        for (int m = 0; m < e2.size(); m++) {
                                            Element e3 = e2.get(m);
                                            // 获得 FILENAME DATAHANDLER
                                            if ("FILENAME".equals(e3.getName())) {
                                                fileName = e3.getText();
                                            } else if ("DATAHANDLER".equals(e3.getName())) {
                                                dataHanlerd = e3.getText();
                                            }
                                        }
                                        // 将二进制字符串转为字节
                                        BASE64Decoder decoder = new BASE64Decoder();
                                        byte[] b = decoder.decodeBuffer(dataHanlerd);
                                        InputStream in = new ByteArrayInputStream(b);
                                        //
                                        StreamingData streamingData = new StreamingData();
                                        streamingData.setFileName(fileName);
                                        streamingData.setDataHandler(new DataHandler(new InputStreamDataSource(in,
                                                "octet-stream")));

                                        streamingDatas.add(streamingData);
                                    }
                                    // 将STREAMINGDATAS集合添加到dataItem中
                                    dataItem.setStreamingDatas(streamingDatas);
                                }
                            }
                            // 将DXDataItem对象添加到DXDataItem集合中
                            dataItems.add(dataItem);
                        }
                        // 将 DXDataItem集合存入request
                        request.setDataList(dataItems);
                    }
                }
            } catch (Exception e) {
                logger.error("DataExchangWebServiceWithoutCAImpl → 将所传入的xml转为dxRequest异常："
                        + ExceptionUtils.getFullStackTrace(e));
                throw new RuntimeException(e.getMessage());
            }
            /****************将所传入的xml转为DxRequest对象结束********************/
            // 验证数据是否满足业务需求
            ExchangeDataConfigService exchangeDataConfigService = ApplicationContextHolder
                    .getBean(ExchangeDataConfigService.class);
            ExchangeDataType exchangeDataType = exchangeDataConfigService.getExType(request.getTypeId());// 数据类型
            Map<String, BusinessHandleSource> businessHandleSourceMap = ApplicationContextHolder
                    .getApplicationContext().getBeansOfType(BusinessHandleSource.class);
            ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                    .getBean(ExchangeDataFlowService.class);
            for (DXDataItem d : request.getDataList()) {
                Map<Integer, String> backMap = businessHandleSourceMap.get(
                        exchangeDataType.getBusinessId().split(":")[0]).correctnessData(d.getText(), request);
                for (Map.Entry<Integer, String> entry : backMap.entrySet()) {
                    Integer key = entry.getKey();
                    if (key == -100 || key == -101) {
                        DXResponse response = new DXResponse();
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
                        return exchangeDataFlowService.turnDXResponseToXml(response);
                    } else if (key != 0) {// 验证不成功时直接返回验证失败的结果
                        DXResponse response = new DXResponse();
                        response.setCode(-1);
                        response.setMsg(entry.getValue());

                        dxResponseXml = exchangeDataFlowService.turnDXResponseToXml(response);
                        logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxSend 返回参数:--------【"
                                + dxResponseXml + "】，批次ID为：" + request.getBatchId());
                        return dxResponseXml;
                    }
                }
            }
            try {
                checkAndPrepare(request.getFrom());
                // // 附件数据解密等处理
                // dxDecryptedAndPrepareStreamingData(request);
                DXResponse response = null;
                response = exchangeDataFlowService.dxSend(request, ExchangeConfig.SOURCE_EXTERNAL);
                dxResponseXml = exchangeDataFlowService.turnDXResponseToXml(response);
                logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxSend 【成功】 返回参数:--------【" + dxResponseXml
                        + "】，批次ID为：" + request.getBatchId());
            } catch (BusinessException b) {
                DXResponse response = new DXResponse();
                response.setCode(-1);
                response.setMsg(b.getMessage());

                dxResponseXml = exchangeDataFlowService.turnDXResponseToXml(response);
                logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxSend 【异常】 返回参数:--------【" + dxResponseXml
                        + "】，批次ID为：" + request.getBatchId());
                logger.error(b.getMessage());
                return dxResponseXml;
            } catch (Exception e) {
                DXResponse response = new DXResponse();
                response.setCode(-1);
                response.setMsg(ExceptionUtils.getFullStackTrace(e));
                dxResponseXml = exchangeDataFlowService.turnDXResponseToXml(response);
                logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxSend 【异常】 返回参数:--------【" + dxResponseXml
                        + "】，批次ID为：" + request.getBatchId());
                logger.error(ExceptionUtils.getStackTrace(e));
                return dxResponseXml;
            }
            return dxResponseXml;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            IgnoreLoginUtils.logout();
        }
        return dxResponseXml;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebServiceWithoutCA#dxCallback(java.lang.String)
     */
    @Override
    public String dxCallback(String dXCallbackRequestXml) {
        logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxCallback 请求参数:--------【" + dXCallbackRequestXml
                + "】");
        ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                .getBean(ExchangeDataFlowService.class);
        DXResponse response = null;
        String dxResponseXml = "";
        DXCallbackRequest request = exchangeDataFlowService.turnXmlToDXCallbackRequest(dXCallbackRequestXml);
        try {
            checkAndPrepare(request.getUnitId());
            response = exchangeDataFlowService.dxCallback(request);
            dxResponseXml = exchangeDataFlowService.turnDXResponseToXml(response);
        } catch (Exception e) {
            response = new DXResponse();
            response.setCode(-2);
            response.setMsg(ExceptionUtils.getStackTrace(e));
            dxResponseXml = exchangeDataFlowService.turnDXResponseToXml(response);
            logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxCallback 【异常】 返回参数:--------【" + dxResponseXml
                    + "】，批次ID为：" + request.getBatchId());
            logger.error(e.getMessage());
        }
        logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → dxCallback 【成功】 返回参数:--------【" + dxResponseXml
                + "】，批次ID为：" + request.getBatchId());
        return dxResponseXml;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebServiceWithoutCA#dxCancel(java.lang.String)
     */
    @Override
    public String dxCancel(String dXCancelRequestXml) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.facade.DataExchangeWebServiceWithoutCA#query(java.lang.String)
     */
    @Override
    public String query(String shareRequestXml) {
        logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → query 请求参数:--------【" + shareRequestXml + "】");
        ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                .getBean(ExchangeDataFlowService.class);

        ShareRequest shareRequest = exchangeDataFlowService.turnXmlToShareRequest(shareRequestXml);
        ShareResponse shareResponse = new ShareResponse();
        String unitId = shareRequest.getUnitId();
        String shareResponseXml = "";
        try {
            checkAndPrepare(unitId);
            // 上传SOAP的XML内容到Mongo
            // String xmlFolderName = typeId + "_" + unitId;
            // String xmlFilename = Calendar.getInstance().getTimeInMillis() +
            // ".xml";
            // uploadXmlToMongo(xmlFolderName, xmlFilename, unitId);
            shareResponse = exchangeDataFlowService.query(shareRequest, "");
            if (shareResponse.getCode() != 0) {// 验证不成功时直接返回验证失败的结果
                shareResponse.setCode(-1);
            } else {
                shareResponse.setCode(1);
            }
            shareResponseXml = exchangeDataFlowService.turnShareResponseToXml(shareResponse);
        } catch (Exception e) {
            shareResponse.setCode(-1);
            shareResponse.setMsg(ExceptionUtils.getStackTrace(e));
            logger.error(e.getMessage());
        } finally {
            logger.error("接入系统调用 DataExchangWebServiceWithoutCAImpl → query 返回参数:--------【" + shareResponseXml + "】");
        }
        return shareResponseXml;
    }

    /**
     * 检验调用源系统单位
     *
     * @param fromUnitId
     */
    private void checkAndPrepare(String fromUnitId) {
        if (StringUtils.isBlank(fromUnitId)) {
            throw new RuntimeException("源单位ID[" + fromUnitId + "]不能为空!");
        }

        String tenantId = getTanentId(fromUnitId);

        // 检验接入系统是否配置
        ExchangeDataConfigService exchangeDataConfigService = ApplicationContextHolder
                .getBean(ExchangeDataConfigService.class);
        ExchangeSystem example = new ExchangeSystem();
        example.setUnitId(fromUnitId);
        List<ExchangeSystem> exchangeSystems = exchangeDataConfigService.findByExample(example);
        if (exchangeSystems.isEmpty()) {
            logger.error("DataExchangWebServiceWithoutCAImpl → checkAndPrepare → 找不到单位ID为[" + fromUnitId + "]的接入系统");
            throw new RuntimeException("找不到单位ID为[" + fromUnitId + "]的接入系统");
        }
        ExchangeSystem exchangeSystem = exchangeSystems.get(0);
        String systemName = exchangeSystem.getName();

        ExchangeDataTypeService exchangeDataTypeService = ApplicationContextHolder
                .getBean(ExchangeDataTypeService.class);
        List<ExchangeDataType> exchangeDataTypes = exchangeDataTypeService.getExchangeDataTypesByTypeIds(exchangeSystem
                .getTypeId());
        if (exchangeDataTypes.isEmpty()) {
            throw new RuntimeException("接入系统[" + systemName + "]的数据类型没有设置业务类型");
        }
        ExchangeDataType exchangeDataType = exchangeDataTypes.get(0);

        UnitApiFacade unitApiFacade = ApplicationContextHolder.getBean(UnitApiFacade.class);
        // 获取单位业务负责人
        List<OrgUserVo> users = unitApiFacade.getBusinessUnitManagerById(exchangeDataType.getBusinessTypeId(),
                fromUnitId);
        if (users.isEmpty()) {
            throw new RuntimeException("接入系统[" + systemName + "]所在单位没有设置业务管理员");
        }
        OrgUserVo user = users.get(0);
        // 模拟单位用户登录
        if (!isCommunal()) {
            TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
            Tenant tenant = null;
            tenant = tenantService.getByAccount(tenantId);
            UserDetails userDetails = new DefaultUserDetails(tenant, user, AuthorityUtils.createAuthorityList());
            CommonUnit commonUnit = unitApiFacade.getCommonUnitById(fromUnitId);
            CommonUnit unit = new CommonUnit();
            BeanUtils.copyProperties(commonUnit, unit);
            // userDetails.setCommonUnit(exchangeDataType.getBusinessTypeId(),
            // commonUnit);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userDetails,
                    userDetails.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authRequest);
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
}
