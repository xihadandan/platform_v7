/*
 * @(#)2013-11-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeClientWebService;
import com.wellsoft.pt.integration.request.ReplyRequest;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

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
 * 2013-11-11.1	zhulh		2013-11-11		Create
 * </pre>
 * @date 2013-11-11
 */
public class Client7 {

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
        String address = "http://192.168.0.53:8080/wellpt-web/wsfacade/oa_dev/client";
        factory.setAddress(address);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "client1");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser", "server");
        outProps.put("encryptionPropFile", "ca/client1Store.properties");
        outProps.put("signatureUser", "client1");
        outProps.put("signaturePropFile", "ca/client1Store.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        factory.getOutInterceptors().add(outInterceptor);

        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/client1Store.properties");
        inProps.put("signaturePropFile", "ca/client1Store.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeClientWebService clientWebService = (DataExchangeClientWebService) factory.create();

        ReplyRequest message = new ReplyRequest();
        // 发送成功的数据ID
        message.setDataId("322343323");
        // 数据版本号
        message.setRecVer(1);
        // 接收/拒收动作标识（接收(1)/拒收(-1)）
        message.setCode(1);
        // 说明
        message.setMsg("接收");

        // 发送请求
        System.out.println(clientWebService.replyMsg(message));
    }

}
