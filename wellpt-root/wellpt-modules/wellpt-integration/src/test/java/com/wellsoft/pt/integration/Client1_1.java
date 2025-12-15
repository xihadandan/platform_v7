/*
 * @(#)2013-11-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeWebService;
import com.wellsoft.pt.integration.request.SendRequest;
import com.wellsoft.pt.integration.response.SendResponse;
import com.wellsoft.pt.integration.support.DataItem;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.io.FileInputStream;
import java.util.*;

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
public class Client1_1 {

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
        // http://10.24.36.53:8080/wellpt-web/wsfacade/xzsp/data/exchange/client
        // String address = "http://10.24.36.53:8080/wellpt-web/wsfacade/xzsp/data/exchange?wsdl";
        // String address = "http://10.23.87.12:81/wsfacade/xzsp/data/exchange?wsdl";
        String address = "http://10.24.36.249:3000/wsfacade/xzsp/data/exchange?wsdl";
        factory.setAddress(address);
        // 启用附件流数据上传
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mtom-enabled", Boolean.TRUE);
        factory.setProperties(properties);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "5d55817edcd3631ac880740d20255fe9_35f7f02c-8a29-4c35-aa72-083cf6111b4d");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser", "766446c13fabe57a67ba6c93243ebbda_35f7f02c-8a29-4c35-aa72-083cf6111b4d");
        outProps.put("encryptionPropFile", "ca/clientStore-WSSP.properties");
        outProps.put("signatureUser", "5d55817edcd3631ac880740d20255fe9_35f7f02c-8a29-4c35-aa72-083cf6111b4d");
        outProps.put("signaturePropFile", "ca/clientStore-WSSP.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        // 启用附件流数据上传
        outInterceptor.setAllowMTOM(true);
        factory.getOutInterceptors().add(outInterceptor);
        //org.apache.ws.security.components.crypto.Merlin
        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/clientStore-WSSP.properties");
        inProps.put("signaturePropFile", "ca/clientStore-WSSP.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeWebService webService = (DataExchangeWebService) factory.create();

        // 请求对象
        SendRequest request = new SendRequest();
        // 数据交换源结点ID(工商局ID)
        request.setFrom("004139106");
        // 数据交目标结点ID(民政局ID)  004138701B
        request.setTo("004140203");
        // 请求数据类型ID
        request.setTypeId("000000000XK");
        // 请求数据批准号
        request.setBatchId("000000000XK_" + UUID.randomUUID());
        // 请求业务数据列表
        List<DataItem> dataList = new ArrayList<DataItem>();
        // 请求业务数据
        DataItem item = new DataItem();
        // 数据统一查询号
        item.setDataId("000000000XK_" + UUID.randomUUID());
        // 数据版本号
        item.setRecVer(1);
        // 请求表单数据，以XML文本形式传输

        String path = "D:/well-soft/workspace/wellpt-integration/src/test/resources/02 商事行政许可信息_000000000XK.xml";
        FileInputStream input = new FileInputStream(path);
        // 请求表单数据，以XML文本形式传输
        String temp = IOUtils.toString(input);
        System.out.println(temp);
        item.setText(temp);
        //				// 请求附件数据
        //				List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
        //				// 附件1
        //				streamingDatas.add(new StreamingData(new DataHandler(new FileDataSource(new File("e:\\TEST.txt")))));
        //				item.setStreamingDatas(streamingDatas);

        // 请求附件数据
        //		List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
        //		// 附件1
        //		streamingDatas.add(new StreamingData(new DataHandler(new FileDataSource(new File("e:\\新建文本文档.txt")))));
        //		item.setStreamingDatas(streamingDatas);
        // 附件2
        //		streamingDatas.add(new StreamingData(new DataHandler(new FileDataSource(new File(
        //				"F:\\eclipse-jee-helios-SR2-win32-x86_64.zip")))));
        //		item.setStreamingDatas(streamingDatas);
        //		dataList.add(item);
        dataList.add(item);
        request.setDataList(dataList);
        System.out.println(" 发送  ：BatchId = " + request.getBatchId() + " From= " + request.getFrom() + " TypeId = "
                + request.getTypeId());
        // 调用WebService发送
        SendResponse response = webService.send(request);
        System.out.println(" 返回  ：BatchId = " + response.getBatchId() + " Code = " + response.getCode() + " Msg="
                + response.getMsg());
    }
}
