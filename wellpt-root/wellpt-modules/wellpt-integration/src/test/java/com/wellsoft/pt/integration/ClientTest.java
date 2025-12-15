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
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

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
public class ClientTest {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // 调用WebService
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(DataExchangeWebService.class);
        // WebService地址
        String address = "http://10.23.87.12:81/wsfacade/XZSP/data/exchange";
        factory.setAddress(address);
        // 启用附件流数据上传
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mtom-enabled", Boolean.TRUE);
        factory.setProperties(properties);

        // 发送数据用户名密码、签名加密
        Map<String, Object> outProps = new HashMap<String, Object>();
        outProps.put("action", "Signature Encrypt");
        outProps.put("user", "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        outProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        outProps.put("encryptionUser", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("encryptionPropFile", "ca/clientStore-QYDJ.properties");
        outProps.put("signatureUser", "63039bbc6650f9dcee84e5e5e281e35e_4b3040ee-d911-4941-865d-9e84083c565f");
        outProps.put("signaturePropFile", "ca/clientStore-QYDJ.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        // 启用附件流数据上传
        outInterceptor.setAllowMTOM(true);
        factory.getOutInterceptors().add(outInterceptor);
        factory.getOutInterceptors().add(outInterceptor);
        //org.apache.ws.security.components.crypto.Merlin
        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ClientPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/clientStore-QYDJ.properties");
        inProps.put("signaturePropFile", "ca/clientStore-QYDJ.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));

        DataExchangeWebService webService = (DataExchangeWebService) factory.create();

        // 请求对象
        SendRequest request = new SendRequest();
        // 数据交换源结点ID(工商局ID)
        request.setFrom("004140203A");
        // 数据交目标结点ID(民政局ID)  004138701B
        request.setTo("");
        // 请求数据类型ID
        request.setTypeId("004140203SZ");
        // 请求数据批准号
        request.setBatchId("BATCHID_002" + UUID.randomUUID());
        // 请求业务数据列表
        List<DataItem> dataList = new ArrayList<DataItem>();
        // 请求业务数据
        DataItem item = new DataItem();
        // 数据统一查询号
        item.setDataId("DATAID_002" + UUID.randomUUID());
        // 数据版本号
        item.setRecVer(1);
        // 请求表单数据，以XML文本形式传输

        // 请求表单数据，以XML文本形式传输
        String temp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><item><rjzczb><!--认缴注册资本-->123</rjzczb><QTWJ>其他文件</QTWJ><QSZDZ>清算组联系地址</QSZDZ><ZS>住所</ZS><ZZJG>组织机构</ZZJG><LXDH><!--联系电话--></LXDH><ZCH>123456789</ZCH><LLRXX><!--联络人信息--></LLRXX><XKJYHW><!--许可经营范围--></XKJYHW><JYCS><!--经营场所--></JYCS><ZTZT><!--主体状态--></ZTZT><ZC><!--章程--></ZC><XGXKDW><!--分发相关许可单位--></XGXKDW><LSZTMC><!--隶属商事主体名称--></LSZTMC><GDXX><!--股东信息--></GDXX><JYHW><!--经营范围--></JYHW><HZRQ><!--核准日期--></HZRQ><ZTMC>商事主体信息</ZTMC><DJJG><!--登记机关--></DJJG><QSZDH><!--清算组联系电话--></QSZDH><ZTLX><!--商事主体类型--></ZTLX><QSZFZR><!--清算组负责人--></QSZFZR><FZJG><!--分支机构信息--></FZJG><SBSSZBDW><!--申报实收资本单位--></SBSSZBDW><body_col><!--null--></body_col><NBQK><!--年报情况--></NBQK><QSZRY><!--清算组人员姓名--></QSZRY><FDDBR><!--法定代表人--></FDDBR><ZYXMLB><!--主营项目类别--></ZYXMLB><fileupload isAttachment=\"1\"><!--附件--></fileupload><RJZCZBDW><!--认缴注册资本单位--></RJZCZBDW><QSZBARQ><!--清算组备案时间--></QSZBARQ><SBSSZB><!--申报实收资本--></SBSSZB><CLRC><!--成立日期--></CLRC><YYQX><!--营业期限--></YYQX><userform_ssxx_gdxx isList=\"1\"><!--商事登记_主体的股东信息--><item><GDMC/><!--股东名称--><GDLX/><!--股东类型--><GDGJ/><!--国籍--><TZE/><!--投资额--><TZEDW/><!--投资额单位--><DZE/><!--到资额--><ZB/><!--占比--></item></userform_ssxx_gdxx><userform_ssxx_zzjg isList=\"1\"><!--商事登记_主体组织机构信息--><item><XM/><!--姓名--><ZW/><!--职务--></item></userform_ssxx_zzjg><userform_ssxx_fzjg isList=\"1\"><!--商事登记_主体分支机构信息--><item><MC/><!--名称--><JYCS/><!--经营场所--><FZR/><!--负责人--><ZT/><!--状态--></item></userform_ssxx_fzjg><userform_ssxx_xgxk isList=\"1\"><!--商事主体登记_相关许可单位--><item><XKJYXMMC/><!--许可经营项目名称--><XKJYXMDM/><!--许可经营项目代码--><DWMC/><!--单位名称--><DWDM/><!--单位代码--></item></userform_ssxx_xgxk></item>";
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
