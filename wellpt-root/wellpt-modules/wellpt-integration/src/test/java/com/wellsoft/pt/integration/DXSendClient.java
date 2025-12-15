/*
 * @(#)2013-11-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration;

import com.wellsoft.pt.integration.facade.DataExchangeWebService;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.response.DXResponse;
import com.wellsoft.pt.integration.support.DXDataItem;
import com.wellsoft.pt.integration.support.StreamingData;
import com.wellsoft.pt.integration.support.WSS4JOutAttachment;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.io.File;
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
public class DXSendClient {

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
        String address = "http://10.24.36.52:8080/wellpt-web/wsfacade/xzsp/data/exchange?wsdl";
        //		String address = "http://10.24.36.52:8080/wellpt-web/webservices/data/exchange?wsdl";
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
        outProps.put("encryptionUser", "server");
        outProps.put("encryptionPropFile", "ca/server-keystore.properties");
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

        DXRequest request = new DXRequest();
        // 数据交换源结点ID(厦门市规划局)
        request.setFrom("004140203");
//		request.setFrom("004139456");
        // 数据交目标结点ID(民政局ID)
        request.setTo("426605496");
        // 请求数据类型ID
        request.setTypeId("SPGC_CW");
        // 请求数据批准号
        request.setBatchId("BATCHID_201406091459_" + UUID.randomUUID());
        // 请求业务数据列表
        List<DXDataItem> dataList = new ArrayList<DXDataItem>();
        // 请求业务数据
        DXDataItem item = new DXDataItem();
        // 数据统一查询号
        item.setDataId("DATAID_201406091459_" + UUID.randomUUID());
        // 数据版本号
        item.setRecVer(1);
        // 请求表单数据，以XML文本形式传输

        String path = "D:\\dxtest\\审批过程_出文_SPCG_CW.xml";
        FileInputStream input = new FileInputStream(path);
        // 请求表单数据，以XML文本形式传输
        String temp = IOUtils.toString(input);
        System.out.println(temp);

        //		String temp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><item><YWLSH>350200004139456A1601002014070214</YWLSH><SJBBH>1</SJBBH><BLHJ>04</BLHJ><SPSXBH>350203D00000000902014070185</SPSXBH><BLRMC>规划局</BLRMC><BLRZWDM>100001</BLRZWDM><BLRZWMC>办理员</BLRZWMC><SPDWMC>规划局</SPDWMC><SPDWDM>004139456</SPDWDM><BLYJ>同意办理</BLYJ><CNBLSX>10</CNBLSX><CNBJRQ>2014-08-08 06:14:51</CNBJRQ><HZBH>481</HZBH><BLSJ>2014-07-29 06:14:51</BLSJ><BZ></BZ><BYZDA></BYZDA><BYZDB></BYZDB><BYZDC>发改：同意受理!；规划局：同意受理！；国土局：同意受理!</BYZDC><BYZDD></BYZDD></item>";
        item.setText(temp);
        // 请求附件数据
        List<StreamingData> streamingDatas = new ArrayList<StreamingData>();
        // 附件1
        File file1 = new File("D:\\dxtest\\aaa.txt");
        WSS4JOutAttachment wss4jOutAttachment1 = new WSS4JOutAttachment(file1, outProps);
        wss4jOutAttachment1.signAndEncrypt();
        streamingDatas.add(wss4jOutAttachment1.getEncryptedStreamingData());

        File file2 = new File("D:\\dxtest\\bbb.txt");
        WSS4JOutAttachment wss4jOutAttachment2 = new WSS4JOutAttachment(file2, outProps);
        wss4jOutAttachment2.signAndEncrypt();
        streamingDatas.add(wss4jOutAttachment2.getEncryptedStreamingData());

        File file3 = new File("D:\\dxtest\\ccc.txt");
        WSS4JOutAttachment wss4jOutAttachment3 = new WSS4JOutAttachment(file3, outProps);
        wss4jOutAttachment3.signAndEncrypt();
        streamingDatas.add(wss4jOutAttachment3.getEncryptedStreamingData());

        item.setStreamingDatas(streamingDatas);
        dataList.add(item);
        request.setDataList(dataList);
        System.out.println("发送  ：BatchId = " + request.getBatchId() + " From= " + request.getFrom() + " TypeId = "
                + request.getTypeId());
        // 调用WebService发送
        DXResponse response = webService.dxSend(request);
        System.out.println("返回  ：BatchId = " + request.getBatchId() + " Code = " + response.getCode() + " Msg="
                + response.getMsg());
        // 获取项目登记后返回的项目编号
        System.out.println(response.getParams());
    }

}
