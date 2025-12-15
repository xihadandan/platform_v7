/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.MessageCancelRequest;
import com.wellsoft.pt.api.response.MessageCancelResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class ClientTest8 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        //		String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        //		WellptClient wellptClient = new DefaultWellptClient(baseAddress,
        //				"T001", "ldx", "0");
        //		MessageSendRequest request = new MessageSendRequest();
        //		List<String> recevier=new ArrayList<String>();
        //		recevier.add("U0010000001");
        //		request.setRecipients(recevier);
        //		request.setSenderId("U0010000001");
        //		request.setSystemid("sid1");
        //		request.setMsgTemplateId("csxxgs");
        //		request.setRelatedUrl("www.baidu.com");
        //		String datajson="{\"pageNo\":1,\"pageSize\":20,\"flowDefinitionName\":\"测试\",\"category\":null}";
        //		request.setData(datajson);
        //		MessageSendResponse response = wellptClient.execute(request);
        //		response.getMessageId();
        //
        //		System.out.println(JsonUtils.object2Json(request));
        //		System.out.println(JsonUtils.object2Json(response));
        //
        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "ldx", "0");
        MessageCancelRequest cancelrequest = new MessageCancelRequest();
        cancelrequest.setDataUuid("852657c4-3bca-4a39-9ea4-b22f714b9f6e");
        MessageCancelResponse cancelresponse = wellptClient.execute(cancelrequest);
        System.out.println(JsonUtils.object2Json(cancelrequest));
        System.out.println(JsonUtils.object2Json(cancelresponse));
        //
    }

}
