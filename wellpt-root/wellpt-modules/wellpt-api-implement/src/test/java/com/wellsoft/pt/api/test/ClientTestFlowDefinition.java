/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowDefinitionDetailGetReqeust;
import com.wellsoft.pt.api.request.FlowDefinitionGetRequest;
import com.wellsoft.pt.api.request.FlowDefinitionQueryRequest;
import com.wellsoft.pt.api.response.FlowDefinitionDetailGetResponse;
import com.wellsoft.pt.api.response.FlowDefinitionGetResponse;
import com.wellsoft.pt.api.response.FlowDefinitionQueryResponse;

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
public class ClientTestFlowDefinition {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://127.0.0.1:8080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X2lubmVyIiwiYXVkIjoi5rWL6K-V5YaF6YOo57O757ufIiwiZXhwIjoxNTk2ODUyODQwLCJ1bml0IjoiUzAwMDAwMDAwNzAiLCJzeXN0ZW1jb2RlIjoidGVzdF9pbm5lciIsInN5c3RlbW5hbWUiOiLmtYvor5XlhoXpg6jns7vnu58iLCJhdXRob3JpemVhcGlzIjpbIi9hcGkvKioiXSwidW5hdXRob3JpemVhcGlzIjpbXX0.k5I0jZweIbHMcJCJXw0FM5LMQ_tJpFzr77rqGUCnXqM";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 查询流程定义列表
        FlowDefinitionQueryRequest flowDefinitionQueryRequest = new FlowDefinitionQueryRequest();
        // flowDefinitionQueryRequest.setFlowDefinitionName("测试子流程");
        // flowDefinitionQueryRequest.setCategory("00006");
        FlowDefinitionQueryResponse flowDefinitionQueryResponse = wellptClient.execute(flowDefinitionQueryRequest);
        System.out.println(flowDefinitionQueryResponse.getDataList());
        System.out.println("page size: " + flowDefinitionQueryResponse.getDataList().size());
        System.out.println("total: " + flowDefinitionQueryResponse.getTotal());

        // 获取流程定义信息
        FlowDefinitionGetRequest flowDefinitionGetRequest = new FlowDefinitionGetRequest();
        flowDefinitionGetRequest.setFlowDefinitionId(flowDefinitionQueryResponse.getDataList().get(0).getId());
        FlowDefinitionGetResponse flowDefinitionGetResponse = wellptClient.execute(flowDefinitionGetRequest);
        System.out.println(flowDefinitionGetResponse.getData());

        // 获取流程定义详情
        FlowDefinitionDetailGetReqeust flowDefinitionDetailGetReqeust = new FlowDefinitionDetailGetReqeust();
        flowDefinitionDetailGetReqeust.setFlowDefinitionId(flowDefinitionQueryResponse.getDataList().get(0).getId());
        FlowDefinitionDetailGetResponse flowDefinitionDetailGetResponse = wellptClient.execute(flowDefinitionDetailGetReqeust);
        System.out.println(flowDefinitionDetailGetResponse.getData());
    }

}
