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
import com.wellsoft.pt.integration.support.StreamingData;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class Client1 {

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
        // 工商WebService地址
        // http://222.76.242.84:8081/xmgs/data/exchange/client
        // http://10.24.36.53:8080/wellpt-web/wsfacade/xzsp/data/exchange/client
        String address = "http://10.24.36.53:8080/wellpt-web/wsfacade/xzsp/data/exchange?wsdl";
        // String address = "http://10.23.87.12:81/wsfacade/xzsp/data/exchange?wsdl";
        // String address = "http://10.24.36.249:3000/wsfacade/xzsp/data/exchange?wsdl";
        factory.setAddress(address);
        // 启用附件流数据上传
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mtom-enabled", Boolean.TRUE);
        factory.setProperties(properties);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("encryptionPropFile", "ca/serverStore-SSGL.properties");
        outProps.put("signatureUser", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("signaturePropFile", "ca/serverStore-SSGL.properties");
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
        inProps.put("decryptionPropFile", "ca/serverStore-SSGL.properties");
        inProps.put("signaturePropFile", "ca/serverStore-SSGL.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeWebService webService = (DataExchangeWebService) factory.create();

        // 请求对象
        SendRequest request = new SendRequest();
        // 数据交换源结点ID(工商局ID)
        request.setFrom("004140203");
        // 数据交目标结点ID(民政局ID)  004138701B
        request.setTo("004139106");
        // 请求数据类型ID
        request.setTypeId("004140203SZ");
        // 请求数据批准号
        request.setBatchId("BATCHID_002_sss");
        // 请求业务数据列表
        List<DataItem> dataList = new ArrayList<DataItem>();
        // 请求业务数据
        DataItem item = new DataItem();
        // 数据统一查询号
        item.setDataId("DATAID_002_sssf");
        // 数据版本号
        item.setRecVer(1);
        // 请求表单数据，以XML文本形式传输

        String path = "D:/well-soft/workspace/wellpt-integration/src/test/resources/01 商事主体登记信息_004140203SZ.xml";
        FileInputStream input = new FileInputStream(path);
        // 请求表单数据，以XML文本形式传输
        String temp = IOUtils.toString(input);
        System.out.println(temp);
        item.setText(temp);

        // 请求附件数据
        List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
        // 附件1
        //		File file1 = new File("F:\\nexus-2.1.2-bundle.zip");
        //		WSS4JOutAttachment wss4jOutAttachment1 = new WSS4JOutAttachment(file1, outProps);
        //		wss4jOutAttachment1.signAndEncrypt();
        //		streamingDatas.add(wss4jOutAttachment1.getEncryptedStreamingData());
        //
        //		// 附件2
        //		File file2 = new File("F:\\11.doc");
        //		StreamingData streamingData = new StreamingData(new DataHandler(new FileDataSource(file2)));
        //		WSS4JOutAttachment wss4jOutAttachment2 = new WSS4JOutAttachment(streamingData, outProps);
        //		wss4jOutAttachment2.signAndEncrypt();
        //		streamingDatas.add(wss4jOutAttachment2.getEncryptedStreamingData());

        item.setStreamingDatas(streamingDatas);
        dataList.add(item);
        request.setDataList(dataList);
        System.out.println(" 发送  ：BatchId = " + request.getBatchId() + " From= " + request.getFrom() + " TypeId = "
                + request.getTypeId());
        // 调用WebService发送
        SendResponse response = webService.send(request);
        //		wss4jOutAttachment2.release();
        System.out.println(" 返回  ：BatchId = " + response.getBatchId() + " Code = " + response.getCode() + " Msg="
                + response.getMsg());
    }

}
