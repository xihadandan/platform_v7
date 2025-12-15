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
import com.wellsoft.pt.integration.support.DataItem;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.*;

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
public class Client {

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
        outProps.put("user", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        outProps.put("encryptionUser", "02682a3cfcf30c65f4e8c3d569befeeb_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("encryptionPropFile", "ca/serverStore-SSGL.properties");
        outProps.put("signatureUser", "c60252744a363043639081d54f91b046_1742c750-2961-42d4-867b-03f35301fa8a");
        outProps.put("signaturePropFile", "ca/serverStore-SSGL.properties");
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        factory.getOutInterceptors().add(outInterceptor);
        factory.getOutInterceptors().add(new LoggingOutInterceptor());

        // 接收数据用户名密码、签名解密
        Map<String, Object> inProps = new HashMap<String, Object>();
        inProps.put("action", "Signature Encrypt");
        inProps.put("passwordCallbackClass", ServerPasswordCallback.class.getName());
        inProps.put("decryptionPropFile", "ca/serverStore-SSGL.properties");
        inProps.put("signaturePropFile", "ca/serverStore-SSGL.properties");
        factory.getInInterceptors().add(new WSS4JInInterceptor(inProps));
        factory.getInInterceptors().add(new LoggingInInterceptor());

        DataExchangeClientWebService clientWebService = (DataExchangeClientWebService) factory.create();

        // 请求对象
        ReceiveRequest request = new ReceiveRequest();
        // 数据交换源结点ID(工商局ID)
        request.setFrom("004140203");
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
        String temp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><item><RJZCZB><!--认缴注册资本-->123</RJZCZB><QTWJ>其他文件</QTWJ><QSZDZ>清算组联系地址</QSZDZ><ZS>住所</ZS><ZZJG>组织机构</ZZJG><LXDH><!--联系电话--></LXDH><ZCH>123456789</ZCH><LLRXX><!--联络人信息--></LLRXX><XKJYHW><!--许可经营范围--></XKJYHW><JYCS><!--经营场所--></JYCS><ZTZT><!--主体状态--></ZTZT><ZC><!--章程--></ZC><XGXKDW><!--分发相关许可单位--></XGXKDW><LSZTMC><!--隶属商事主体名称--></LSZTMC><GDXX><!--股东信息--></GDXX><JYHW><!--经营范围--></JYHW><HZRQ><!--核准日期--></HZRQ><ZTMC>商事主体信息</ZTMC><DJJG><!--登记机关--></DJJG><QSZDH><!--清算组联系电话--></QSZDH><ZTLX><!--商事主体类型--></ZTLX><QSZFZR><!--清算组负责人--></QSZFZR><FZJG><!--分支机构信息--></FZJG><SBSSZBDW><!--申报实收资本单位--></SBSSZBDW><body_col><!--null--></body_col><NBQK><!--年报情况--></NBQK><QSZRY><!--清算组人员姓名--></QSZRY><FDDBR><!--法定代表人--></FDDBR><ZYXMLB><!--主营项目类别--></ZYXMLB><fileupload isAttachment=\"1\"><!--附件--></fileupload><RJZCZBDW><!--认缴注册资本单位--></RJZCZBDW><QSZBARQ><!--清算组备案时间--></QSZBARQ><SBSSZB><!--申报实收资本--></SBSSZB><CLRC><!--成立日期--></CLRC><YYQX><!--营业期限--></YYQX><userform_ssxx_gdxx isList=\"1\"><!--商事登记_主体的股东信息--><item><GDMC/><!--股东名称--><GDLX/><!--股东类型--><GDGJ/><!--国籍--><TZE/><!--投资额--><TZEDW/><!--投资额单位--><DZE/><!--到资额--><ZB/><!--占比--></item></userform_ssxx_gdxx><userform_ssxx_zzjg isList=\"1\"><!--商事登记_主体组织机构信息--><item><XM/><!--姓名--><ZW/><!--职务--></item></userform_ssxx_zzjg><userform_ssxx_fzjg isList=\"1\"><!--商事登记_主体分支机构信息--><item><MC/><!--名称--><JYCS/><!--经营场所--><FZR/><!--负责人--><ZT/><!--状态--></item></userform_ssxx_fzjg><userform_ssxx_xgxk isList=\"1\"><!--商事主体登记_相关许可单位--><item><XKJYXMMC/><!--许可经营项目名称--><XKJYXMDM/><!--许可经营项目代码--><DWMC/><!--单位名称--><DWDM/><!--单位代码--></item></userform_ssxx_xgxk></item>";
        item.setText(temp);

        dataList.add(item);
        request.setDataList(dataList);

        // 发送请求
        ReceiveResponse response = clientWebService.receive(request);
        System.out.println(response);
    }
}
