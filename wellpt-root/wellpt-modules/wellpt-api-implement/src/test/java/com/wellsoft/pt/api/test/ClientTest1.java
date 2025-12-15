/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowDefinitionGetRequest;
import com.wellsoft.pt.api.response.FlowDefinitionGetResponse;

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
public class ClientTest1 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {

        String baseAddress = "http://localhost:9080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MTExIiwiYXVkIjoidGVzdDExMSIsIm5iZiI6MTU2NTI2NTcyMCwiZXhwIjoxNTY1MjY2MjAwfQ.wyQi4KhusAIvQmAjynfFl6FRLrWYDAP0szdHRxEMVrk";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 1、获取用户权限
        System.out.println("获取用户权限");
        FlowDefinitionGetRequest taskDetailForHlGetRequest = new FlowDefinitionGetRequest();
        taskDetailForHlGetRequest.setFlowDefinitionId("OA_ODM_FW_FLOW");
        FlowDefinitionGetResponse response = wellptClient.execute(taskDetailForHlGetRequest);
        System.out.println(response.getMsg());
        System.out.println(response.getData());

        // 1、获取用户权限
        //		System.out.println("获取用户组织信息");
        //		OrgUserOrgInfoQueryRequest orgUserOrgInfoQueryRequest = new OrgUserOrgInfoQueryRequest();
        //		orgUserOrgInfoQueryRequest.setUserCode("chenl");
        //		OrgUserOrgInfoQueryResponse response1 = wellptClient.execute(orgUserOrgInfoQueryRequest);
        //		System.out.println(response1);

    }

}
