/*
 * @(#)2013-11-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeWebService;
import com.wellsoft.pt.integration.request.ReplyRequest;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: f、目标系统在确认接收/拒收后调用数据交换平台回复消息接口
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
public class Client6 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // 调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DataExchangeWebService.class);
        // 数据交换目标结点(工商局)自建系统WebService地址
        String address = "http://192.168.0.52:8080/wellpt-web/wsfacade/xzsp/data/exchange";
        factory.setAddress(address);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "9c85bc0910de18c35a59f14f8d1a8c79_35f7f02c-8a29-4c35-aa72-083cf6111b4d");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser", "766446c13fabe57a67ba6c93243ebbda_35f7f02c-8a29-4c35-aa72-083cf6111b4d");
        outProps.put("encryptionPropFile", "ca/clientStore-QYDJ.properties");
        outProps.put("signatureUser", "9c85bc0910de18c35a59f14f8d1a8c79_35f7f02c-8a29-4c35-aa72-083cf6111b4d");
        outProps.put("signaturePropFile", "ca/clientStore-QYDJ.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        factory.getOutInterceptors().add(outInterceptor);

        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/clientStore-QYDJ.properties");
        inProps.put("signaturePropFile", "ca/clientStore-QYDJ.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeWebService webService = (DataExchangeWebService) factory.create();

        ReplyRequest message = new ReplyRequest();
        // 发送成功的数据ID
        message.setDataId("DATAID_002d4064b00-7a12-4984-8b7d-629f01a09f48");
        // 数据版本号
        message.setRecVer(1);
        // 接收/拒收动作标识（接收(1)/拒收(-1)）
        message.setCode(1);
        // 说明
        message.setMsg("签收");

        message.setUnitId("004139106");

        // 发送请求
        System.out.println(webService.replyMsg(message));
    }

}
