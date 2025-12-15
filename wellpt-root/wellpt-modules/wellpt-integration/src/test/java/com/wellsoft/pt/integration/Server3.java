/*
 * @(#)2013-11-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.request.ReceiveRequest;
import com.wellsoft.pt.integration.response.ReceiveResponse;
import com.wellsoft.pt.integration.security.ServerPasswordCallback;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: c、目标系统接收数据交换平台数据接收接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-11.1	zhulh		2013-11-11		Create
 * </pre>
 * @date 2013-11-11
 */
public class Server3 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DataExchangeClientWebService.class);
        // 数据交换目标结点(工商局)自建系统WebService地址
        String address = "http://10.24.36.197/services/dataExchangeClientWebService?wsdl";
        factory.setAddress(address);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "server");
        outProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        outProps.put("encryptionUser", "client1");
        outProps.put("encryptionPropFile", "ca/serverStore-SSGL.properties");
        outProps.put("signatureUser", "server");
        outProps.put("signaturePropFile", "ca/serverStore.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        factory.getOutInterceptors().add(outInterceptor);

        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/serverStore-SSGL.properties");
        inProps.put("signaturePropFile", "ca/serverStore-SSGL.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeClientWebService clientWebService = (DataExchangeClientWebService) factory.create();

        // 请求对象
        ReceiveRequest request = new ReceiveRequest();
        // 数据交换源结点ID(工商局ID)
        request.setFrom("004140203");
        // 数据交目标结点ID(民政局ID)
        request.setTo("004139106");
        // 请求数据类型ID
        request.setTypeId("DATATYPE");
        // 请求数据批准号
        request.setBatchId("1" + UUID.randomUUID());

        // 发送请求
        ReceiveResponse response = clientWebService.receive(request);
        System.out.println(response);
    }
}
