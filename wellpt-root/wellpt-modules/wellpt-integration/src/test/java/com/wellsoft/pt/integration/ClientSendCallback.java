/*
 * @(#)2013-12-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.request.ClientSendCallbackRequest;
import com.wellsoft.pt.integration.response.ClientSendCallbackResponse;
import com.wellsoft.pt.integration.security.ServerPasswordCallback;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-13.1	zhulh		2013-12-13		Create
 * </pre>
 * @date 2013-12-13
 */
public class ClientSendCallback {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // 调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DataExchangeClientWebService.class);
        // 数据交换目标结点(工商局)自建系统WebService地址
        String address = "http://10.24.36.53:8080/wellpt-web/wsfacade/xzsp/data/exchange/client?wsdl";
        // String address = "http://222.76.242.84:8081/xmgs/data/exchange/client";
        // String address = "http://10.23.87.50:8081/xmgs/data/exchange/client";
        factory.setAddress(address);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        outProps.put("encryptionUser", "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        outProps.put("encryptionPropFile", "ca/serverStore-SSGL.properties");
        outProps.put("signatureUser", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("signaturePropFile", "ca/serverStore-SSGL.properties");
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
        ClientSendCallbackRequest request = new ClientSendCallbackRequest();
        request.setBatchId("123");
        // 请求数据批准号
        request.setBatchId("1" + UUID.randomUUID());

        // 发送请求
        ClientSendCallbackResponse response = clientWebService.clientSendCallback(request);
        System.out.println(response.getMsg());
        System.out.println(response.getCode());
    }
}
