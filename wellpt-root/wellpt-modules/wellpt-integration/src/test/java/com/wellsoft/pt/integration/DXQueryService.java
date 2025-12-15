/*
 * @(#)2013-11-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeWebService;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.response.ShareResponse;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: a、源系统调用数据交换平台上传接口
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
public class DXQueryService {

    /**
     * 如何描述该方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DataExchangeWebService.class);
        // WebService地址
        //		String address = "http://10.24.36.52:8080/wellpt-web/wsfacade/xzsp/data/exchange?wsdl";
        String address = "http://10.24.36.52:8080/wellpt-web/webservices/data/exchange/client/wssp?wsdl";
        factory.setAddress(address);
        // 启用附件流数据上传
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mtom-enabled", Boolean.TRUE);
        factory.setProperties(properties);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "client");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser", "client");
        outProps.put("encryptionPropFile", "ca/client-keystore.properties");
        outProps.put("signatureUser", "client");
        outProps.put("signaturePropFile", "ca/client-keystore.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        // 启用附件流数据上传
        outInterceptor.setAllowMTOM(true);
        factory.getOutInterceptors().add(outInterceptor);
        factory.getOutInterceptors().add(new LoggingOutInterceptor());

        //org.apache.ws.security.components.crypto.Merlin
        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/client-keystore.properties");
        inProps.put("signaturePropFile", "ca/client-keystore.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeWebService webService = (DataExchangeWebService) factory.create();
        // 调用WebService发送

        ShareRequest shareRequest = new ShareRequest();
        shareRequest.setTypeId("004140203SZ");
        shareRequest.setPageSize(10);
        shareRequest.setCurrentPage(1);
        shareRequest.setUnitId("004140203");
        ShareResponse response = webService.query(shareRequest);
        System.out.println("请求queryShareData成功");
    }
}
